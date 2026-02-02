package brama.pressing_api.chat.service.impl;

import brama.pressing_api.chat.ChatMapper;
import brama.pressing_api.chat.domain.ChatAttachment;
import brama.pressing_api.chat.domain.ChatMessage;
import brama.pressing_api.chat.domain.ChatMessageReceipt;
import brama.pressing_api.chat.domain.ChatMessageStatus;
import brama.pressing_api.chat.domain.ChatMessageType;
import brama.pressing_api.chat.domain.Conversation;
import brama.pressing_api.chat.domain.UserPresence;
import brama.pressing_api.chat.dto.request.CreateConversationRequest;
import brama.pressing_api.chat.dto.request.EditChatMessageRequest;
import brama.pressing_api.chat.dto.request.SendChatMessageRequest;
import brama.pressing_api.chat.dto.response.ChatConversationResponse;
import brama.pressing_api.chat.dto.response.ChatMessageResponse;
import brama.pressing_api.chat.notification.PushNotificationService;
import brama.pressing_api.chat.repo.ChatMessageRepository;
import brama.pressing_api.chat.repo.ConversationRepository;
import brama.pressing_api.chat.service.ChatService;
import brama.pressing_api.chat.service.UserPresenceService;
import brama.pressing_api.chat.websocket.ChatReceiptEvent;
import brama.pressing_api.chat.websocket.ChatTypingEvent;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.user.User;
import brama.pressing_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private static final int PREVIEW_LIMIT = 120;

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserPresenceService presenceService;
    private final PushNotificationService pushNotificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public ChatConversationResponse createConversation(final CreateConversationRequest request, final String userId) {
        if (request == null || StringUtils.isBlank(request.getUserId())) {
            throw new BusinessException(ErrorCode.CHAT_INVALID_REQUEST);
        }
        String otherUserId = request.getUserId();
        if (userId.equals(otherUserId)) {
            throw new BusinessException(ErrorCode.CHAT_INVALID_REQUEST);
        }
        User currentUser = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherUserId);
        enforceAdminClientAccess(currentUser, otherUser);

        Conversation conversation = getOrCreateConversation(userId, otherUser);
        UserPresence presence = presenceService.findByUserIds(List.of(otherUserId))
                .get(otherUserId);
        return ChatMapper.toConversationResponse(conversation, otherUser, presence, userId);
    }

    @Override
    public List<ChatConversationResponse> listConversations(final String userId) {
        User currentUser = getUserOrThrow(userId);
        if (isAdmin(currentUser)) {
            return listAdminConversations(currentUser);
        }
        return listClientConversations(currentUser);
    }

    @Override
    public Page<ChatMessageResponse> listMessages(final String conversationId,
                                                 final Pageable pageable,
                                                 final String userId) {
        Conversation conversation = getConversationForUser(conversationId, userId);
        if (conversation == null) {
            throw new EntityNotFoundException("Conversation not found");
        }
        return messageRepository.findByConversationIdOrderByCreatedDateDesc(conversationId, pageable)
                .map(ChatMapper::toMessageResponse);
    }

    @Override
    public ChatMessageResponse sendMessage(final SendChatMessageRequest request, final String senderId) {
        validateMessageRequest(request);

        User sender = getUserOrThrow(senderId);
        Conversation conversation = resolveConversation(request, sender);
        String recipientId = resolveRecipient(request, conversation, sender);

        List<ChatAttachment> attachments = ChatMapper.toAttachments(request.getAttachments());
        ChatMessageType type = resolveType(request.getType(), request.getContent(), attachments);

        LocalDateTime now = LocalDateTime.now();
        Map<String, ChatMessageReceipt> receipts = new HashMap<>();
        receipts.put(senderId, ChatMessageReceipt.builder()
                .status(ChatMessageStatus.READ)
                .timestamp(now)
                .build());
        receipts.put(recipientId, ChatMessageReceipt.builder()
                .status(ChatMessageStatus.SENT)
                .timestamp(now)
                .build());

        ChatMessage message = ChatMessage.builder()
                .conversationId(conversation.getId())
                .senderId(senderId)
                .recipientId(recipientId)
                .type(type)
                .content(request.getContent())
                .attachments(attachments)
                .receipts(receipts)
                .deleted(false)
                .build();

        ChatMessage saved = messageRepository.save(message);
        updateConversationForMessage(conversation, saved, recipientId);
        presenceService.touch(senderId);

        ChatMessageResponse response = ChatMapper.toMessageResponse(saved);
        messagingTemplate.convertAndSendToUser(recipientId, "/queue/messages", response);
        messagingTemplate.convertAndSendToUser(senderId, "/queue/messages", response);

        if (!presenceService.isOnline(recipientId)) {
            pushNotificationService.sendChatMessageNotification(recipientId, conversation, saved);
        }

        return response;
    }

    @Override
    public ChatMessageResponse editMessage(final String messageId,
                                           final EditChatMessageRequest request,
                                           final String userId) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
        if (!userId.equals(message.getSenderId())) {
            throw new BusinessException(ErrorCode.CHAT_MESSAGE_EDIT_NOT_ALLOWED);
        }
        if (message.isDeleted()) {
            throw new BusinessException(ErrorCode.CHAT_MESSAGE_DELETED);
        }

        List<ChatAttachment> attachments = ChatMapper.toAttachments(request.getAttachments());
        ChatMessageType type = resolveType(request.getType(), request.getContent(), attachments);

        message.setContent(request.getContent());
        message.setAttachments(attachments);
        message.setType(type);
        message.setEditedAt(LocalDateTime.now());

        ChatMessage saved = messageRepository.save(message);
        updateConversationPreviewIfNeeded(saved);

        ChatMessageResponse response = ChatMapper.toMessageResponse(saved);
        notifyParticipants(saved.getConversationId(), response, "/queue/messages");
        return response;
    }

    @Override
    public ChatMessageResponse deleteMessage(final String messageId, final String userId) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
        if (!userId.equals(message.getSenderId())) {
            throw new BusinessException(ErrorCode.CHAT_MESSAGE_DELETE_NOT_ALLOWED);
        }
        if (message.isDeleted()) {
            return ChatMapper.toMessageResponse(message);
        }

        message.setDeleted(true);
        message.setDeletedAt(LocalDateTime.now());
        message.setDeletedBy(userId);
        message.setContent(null);
        message.setAttachments(Collections.emptyList());

        ChatMessage saved = messageRepository.save(message);
        updateConversationPreviewIfNeeded(saved);

        ChatMessageResponse response = ChatMapper.toMessageResponse(saved);
        notifyParticipants(saved.getConversationId(), response, "/queue/messages");
        return response;
    }

    @Override
    public void markConversationRead(final String conversationId, final String userId) {
        Conversation conversation = getConversationForUser(conversationId, userId);
        List<ChatMessage> messages = messageRepository
                .findByConversationIdAndRecipientId(conversationId, userId);

        LocalDateTime now = LocalDateTime.now();
        List<ChatMessage> updated = new ArrayList<>();
        for (ChatMessage message : messages) {
            if (updateReceipt(message, userId, ChatMessageStatus.READ, now)) {
                updated.add(message);
            }
        }

        if (!updated.isEmpty()) {
            messageRepository.saveAll(updated);
        }

        resetUnreadCount(conversation, userId);

        String otherUserId = getOtherParticipant(conversation, userId);
        if (StringUtils.isNotBlank(otherUserId)) {
            ChatReceiptEvent event = ChatReceiptEvent.builder()
                    .conversationId(conversationId)
                    .userId(userId)
                    .status(ChatMessageStatus.READ)
                    .timestamp(now)
                    .build();
            messagingTemplate.convertAndSendToUser(otherUserId, "/queue/receipts", event);
        }
    }

    @Override
    public void markConversationDelivered(final String conversationId, final String userId) {
        Conversation conversation = getConversationForUser(conversationId, userId);
        List<ChatMessage> messages = messageRepository
                .findByConversationIdAndRecipientId(conversationId, userId);

        LocalDateTime now = LocalDateTime.now();
        List<ChatMessage> updated = new ArrayList<>();
        for (ChatMessage message : messages) {
            if (updateReceipt(message, userId, ChatMessageStatus.DELIVERED, now)) {
                updated.add(message);
            }
        }
        if (!updated.isEmpty()) {
            messageRepository.saveAll(updated);
        }

        String otherUserId = getOtherParticipant(conversation, userId);
        if (StringUtils.isNotBlank(otherUserId)) {
            ChatReceiptEvent event = ChatReceiptEvent.builder()
                    .conversationId(conversationId)
                    .userId(userId)
                    .status(ChatMessageStatus.DELIVERED)
                    .timestamp(now)
                    .build();
            messagingTemplate.convertAndSendToUser(otherUserId, "/queue/receipts", event);
        }
    }

    @Override
    public void sendTyping(final String conversationId, final String userId, final boolean typing) {
        Conversation conversation = getConversationForUser(conversationId, userId);
        String otherUserId = getOtherParticipant(conversation, userId);
        if (StringUtils.isBlank(otherUserId)) {
            return;
        }
        ChatTypingEvent event = ChatTypingEvent.builder()
                .conversationId(conversationId)
                .userId(userId)
                .typing(typing)
                .timestamp(LocalDateTime.now())
                .build();
        messagingTemplate.convertAndSendToUser(otherUserId, "/queue/typing", event);
    }

    private void validateMessageRequest(final SendChatMessageRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.CHAT_INVALID_REQUEST);
        }
        boolean hasContent = StringUtils.isNotBlank(request.getContent());
        boolean hasAttachments = request.getAttachments() != null && !request.getAttachments().isEmpty();
        if (!hasContent && !hasAttachments) {
            throw new BusinessException(ErrorCode.CHAT_MESSAGE_EMPTY);
        }
        if (StringUtils.isBlank(request.getConversationId()) && StringUtils.isBlank(request.getRecipientId())) {
            throw new BusinessException(ErrorCode.CHAT_INVALID_REQUEST);
        }
    }

    private Conversation resolveConversation(final SendChatMessageRequest request, final User sender) {
        if (StringUtils.isNotBlank(request.getConversationId())) {
            return getConversationForUser(request.getConversationId(), sender.getId());
        }
        String recipientId = request.getRecipientId();
        if (StringUtils.isBlank(recipientId)) {
            throw new BusinessException(ErrorCode.CHAT_INVALID_REQUEST);
        }
        if (sender.getId().equals(recipientId)) {
            throw new BusinessException(ErrorCode.CHAT_INVALID_REQUEST);
        }
        User recipient = getUserOrThrow(recipientId);
        enforceAdminClientAccess(sender, recipient);
        return getOrCreateConversation(sender.getId(), recipient);
    }

    private String resolveRecipient(final SendChatMessageRequest request,
                                    final Conversation conversation,
                                    final User sender) {
        if (StringUtils.isNotBlank(request.getRecipientId())) {
            if (!isAdmin(sender)) {
                User recipient = getUserOrThrow(request.getRecipientId());
                if (!isAdmin(recipient)) {
                    throw new BusinessException(ErrorCode.CHAT_ACCESS_DENIED);
                }
            }
            return request.getRecipientId();
        }
        String otherUserId = getOtherParticipant(conversation, sender.getId());
        if (StringUtils.isBlank(otherUserId)) {
            throw new BusinessException(ErrorCode.CHAT_INVALID_REQUEST);
        }
        return otherUserId;
    }

    private Conversation getOrCreateConversation(final String userId, final String otherUserId) {
        User otherUser = getUserOrThrow(otherUserId);
        return getOrCreateConversation(userId, otherUser);
    }

    private Conversation getOrCreateConversation(final String userId, final User otherUser) {
        Optional<Conversation> existing = conversationRepository.findDirectConversation(userId, otherUser.getId());
        if (existing.isPresent()) {
            return existing.get();
        }
        Conversation conversation = Conversation.builder()
                .participantIds(List.of(userId, otherUser.getId()))
                .unreadCounts(new HashMap<>())
                .build();
        return conversationRepository.save(conversation);
    }

    private Conversation getConversationForUser(final String conversationId, final String userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));
        if (conversation.getParticipantIds() == null || !conversation.getParticipantIds().contains(userId)) {
            throw new BusinessException(ErrorCode.CHAT_ACCESS_DENIED);
        }
        User currentUser = getUserOrThrow(userId);
        if (!isAdmin(currentUser)) {
            String otherUserId = getOtherParticipant(conversation, userId);
            if (StringUtils.isBlank(otherUserId)) {
                throw new BusinessException(ErrorCode.CHAT_ACCESS_DENIED);
            }
            User otherUser = getUserOrThrow(otherUserId);
            if (!isAdmin(otherUser)) {
                throw new BusinessException(ErrorCode.CHAT_ACCESS_DENIED);
            }
        }
        return conversation;
    }

    private void updateConversationForMessage(final Conversation conversation,
                                              final ChatMessage message,
                                              final String recipientId) {
        conversation.setLastMessageId(message.getId());
        conversation.setLastMessageSenderId(message.getSenderId());
        conversation.setLastMessageAt(message.getCreatedDate());
        conversation.setLastMessagePreview(buildPreview(message));
        incrementUnreadCount(conversation, recipientId);
        conversationRepository.save(conversation);
    }

    private void updateConversationPreviewIfNeeded(final ChatMessage message) {
        Conversation conversation = conversationRepository.findById(message.getConversationId())
                .orElse(null);
        if (conversation == null) {
            return;
        }
        if (!message.getId().equals(conversation.getLastMessageId())) {
            return;
        }
        conversation.setLastMessagePreview(buildPreview(message));
        conversation.setLastMessageAt(Optional.ofNullable(message.getLastModifiedDate())
                .orElse(message.getCreatedDate()));
        conversationRepository.save(conversation);
    }

    private void incrementUnreadCount(final Conversation conversation, final String userId) {
        Map<String, Integer> unreadCounts = conversation.getUnreadCounts();
        if (unreadCounts == null) {
            unreadCounts = new HashMap<>();
        }
        unreadCounts.put(userId, unreadCounts.getOrDefault(userId, 0) + 1);
        conversation.setUnreadCounts(unreadCounts);
    }

    private void resetUnreadCount(final Conversation conversation, final String userId) {
        Map<String, Integer> unreadCounts = conversation.getUnreadCounts();
        if (unreadCounts == null) {
            unreadCounts = new HashMap<>();
        }
        unreadCounts.put(userId, 0);
        conversation.setUnreadCounts(unreadCounts);
        conversationRepository.save(conversation);
    }

    private boolean updateReceipt(final ChatMessage message,
                                  final String userId,
                                  final ChatMessageStatus status,
                                  final LocalDateTime timestamp) {
        Map<String, ChatMessageReceipt> receipts = message.getReceipts();
        if (receipts == null) {
            receipts = new HashMap<>();
        }
        ChatMessageReceipt current = receipts.get(userId);
        if (!shouldUpdateReceipt(current != null ? current.getStatus() : null, status)) {
            return false;
        }
        receipts.put(userId, ChatMessageReceipt.builder()
                .status(status)
                .timestamp(timestamp)
                .build());
        message.setReceipts(receipts);
        return true;
    }

    private boolean shouldUpdateReceipt(final ChatMessageStatus current, final ChatMessageStatus next) {
        if (current == null) {
            return true;
        }
        if (current == ChatMessageStatus.READ) {
            return false;
        }
        if (current == ChatMessageStatus.DELIVERED && next == ChatMessageStatus.SENT) {
            return false;
        }
        return current != next;
    }

    private void notifyParticipants(final String conversationId,
                                    final ChatMessageResponse payload,
                                    final String destination) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElse(null);
        if (conversation == null || conversation.getParticipantIds() == null) {
            return;
        }
        for (String participantId : conversation.getParticipantIds()) {
            messagingTemplate.convertAndSendToUser(participantId, destination, payload);
        }
    }

    private String getOtherParticipant(final Conversation conversation, final String userId) {
        if (conversation == null || conversation.getParticipantIds() == null) {
            return null;
        }
        return conversation.getParticipantIds().stream()
                .filter(participant -> !participant.equals(userId))
                .findFirst()
                .orElse(null);
    }

    private String buildPreview(final ChatMessage message) {
        if (message == null) {
            return null;
        }
        if (message.isDeleted()) {
            return "Message deleted";
        }
        if (StringUtils.isNotBlank(message.getContent())) {
            return StringUtils.abbreviate(message.getContent(), PREVIEW_LIMIT);
        }
        List<ChatAttachment> attachments = message.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            return message.getType() == ChatMessageType.IMAGE ? "Image" : "Attachment";
        }
        return "Message";
    }

    private ChatMessageType resolveType(final ChatMessageType requested,
                                        final String content,
                                        final List<ChatAttachment> attachments) {
        if (requested != null) {
            return requested;
        }
        if (attachments != null && !attachments.isEmpty()) {
            boolean hasImage = attachments.stream()
                    .anyMatch(attachment -> StringUtils.startsWithIgnoreCase(attachment.getContentType(), "image"));
            return hasImage ? ChatMessageType.IMAGE : ChatMessageType.FILE;
        }
        if (StringUtils.isNotBlank(content)) {
            return ChatMessageType.TEXT;
        }
        return ChatMessageType.SYSTEM;
    }

    private List<ChatConversationResponse> listAdminConversations(final User admin) {
        List<User> clients = userRepository.findAll().stream()
                .filter(user -> user != null && !admin.getId().equals(user.getId()))
                .filter(user -> !isAdmin(user))
                .collect(Collectors.toList());
        if (clients.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, UserPresence> presenceMap = presenceService.findByUserIds(
                clients.stream().map(User::getId).collect(Collectors.toList())
        );

        List<ChatConversationResponse> responses = new ArrayList<>();
        for (User client : clients) {
            Conversation conversation = getOrCreateConversation(admin.getId(), client);
            responses.add(ChatMapper.toConversationResponse(
                    conversation,
                    client,
                    presenceMap.get(client.getId()),
                    admin.getId()
            ));
        }
        responses.sort(Comparator.comparing(ChatConversationResponse::getLastMessageAt,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());
        return responses;
    }

    private List<ChatConversationResponse> listClientConversations(final User client) {
        List<User> admins = userRepository.findAll().stream()
                .filter(this::isAdmin)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
        if (admins.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, User> adminMap = admins.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Map<String, UserPresence> presenceMap = presenceService.findByUserIds(adminMap.keySet());

        List<Conversation> conversations = conversationRepository.findByParticipantIdsContaining(client.getId());
        List<ChatConversationResponse> responses = new ArrayList<>();
        for (Conversation conversation : conversations) {
            String otherUserId = getOtherParticipant(conversation, client.getId());
            User admin = adminMap.get(otherUserId);
            if (admin == null) {
                continue;
            }
            responses.add(ChatMapper.toConversationResponse(
                    conversation,
                    admin,
                    presenceMap.get(otherUserId),
                    client.getId()
            ));
        }

        if (responses.isEmpty()) {
            User admin = admins.get(0);
            Conversation conversation = getOrCreateConversation(client.getId(), admin);
            responses.add(ChatMapper.toConversationResponse(
                    conversation,
                    admin,
                    presenceMap.get(admin.getId()),
                    client.getId()
            ));
        }

        responses.sort(Comparator.comparing(ChatConversationResponse::getLastMessageAt,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());
        return responses;
    }

    private User getUserOrThrow(final String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private boolean isAdmin(final User user) {
        return hasRole(user, "ADMIN");
    }

    private boolean hasRole(final User user, final String role) {
        if (user == null || user.getRoles() == null) {
            return false;
        }
        String target = role.toUpperCase();
        return user.getRoles().stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .map(String::toUpperCase)
                .anyMatch(value -> value.equals(target) || value.equals("ROLE_" + target));
    }

    private void enforceAdminClientAccess(final User currentUser, final User otherUser) {
        if (!isAdmin(currentUser) && !isAdmin(otherUser)) {
            throw new BusinessException(ErrorCode.CHAT_ACCESS_DENIED);
        }
    }
}
