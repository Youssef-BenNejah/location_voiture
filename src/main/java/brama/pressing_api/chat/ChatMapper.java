package brama.pressing_api.chat;

import brama.pressing_api.chat.domain.ChatAttachment;
import brama.pressing_api.chat.domain.ChatMessage;
import brama.pressing_api.chat.domain.UserPresence;
import brama.pressing_api.chat.dto.request.ChatAttachmentRequest;
import brama.pressing_api.chat.dto.response.ChatConversationResponse;
import brama.pressing_api.chat.dto.response.ChatMessageResponse;
import brama.pressing_api.chat.dto.response.ChatPresenceResponse;
import brama.pressing_api.chat.dto.response.ChatUserSummary;
import brama.pressing_api.chat.domain.Conversation;
import brama.pressing_api.user.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ChatMapper {
    private ChatMapper() {
    }

    public static ChatAttachment toAttachment(final ChatAttachmentRequest request) {
        if (request == null) {
            return null;
        }
        return ChatAttachment.builder()
                .publicId(request.getPublicId())
                .url(request.getUrl())
                .secureUrl(request.getSecureUrl())
                .format(request.getFormat())
                .bytes(request.getBytes())
                .fileName(request.getFileName())
                .contentType(request.getContentType())
                .build();
    }

    public static List<ChatAttachment> toAttachments(final List<ChatAttachmentRequest> requests) {
        if (requests == null) {
            return Collections.emptyList();
        }
        return requests.stream()
                .map(ChatMapper::toAttachment)
                .collect(Collectors.toList());
    }

    public static ChatMessageResponse toMessageResponse(final ChatMessage message) {
        if (message == null) {
            return null;
        }
        return ChatMessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .senderId(message.getSenderId())
                .recipientId(message.getRecipientId())
                .type(message.getType())
                .content(message.getContent())
                .attachments(message.getAttachments())
                .receipts(message.getReceipts())
                .createdDate(message.getCreatedDate())
                .lastModifiedDate(message.getLastModifiedDate())
                .editedAt(message.getEditedAt())
                .deleted(message.isDeleted())
                .deletedAt(message.getDeletedAt())
                .deletedBy(message.getDeletedBy())
                .build();
    }

    public static ChatConversationResponse toConversationResponse(final Conversation conversation,
                                                                 final User otherUser,
                                                                 final UserPresence presence,
                                                                 final String currentUserId) {
        if (conversation == null) {
            return null;
        }
        Map<String, Integer> unreadCounts = conversation.getUnreadCounts();
        int unread = unreadCounts != null && currentUserId != null
                ? unreadCounts.getOrDefault(currentUserId, 0)
                : 0;

        return ChatConversationResponse.builder()
                .id(conversation.getId())
                .participant(toUserSummary(otherUser))
                .presence(toPresenceResponse(presence))
                .lastMessageId(conversation.getLastMessageId())
                .lastMessageSenderId(conversation.getLastMessageSenderId())
                .lastMessagePreview(conversation.getLastMessagePreview())
                .lastMessageAt(conversation.getLastMessageAt())
                .unreadCount(unread)
                .build();
    }

    public static ChatUserSummary toUserSummary(final User user) {
        if (user == null) {
            return null;
        }
        return ChatUserSummary.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public static ChatPresenceResponse toPresenceResponse(final UserPresence presence) {
        if (presence == null) {
            return null;
        }
        return ChatPresenceResponse.builder()
                .online(presence.isOnline())
                .lastSeenAt(presence.getLastSeenAt())
                .lastActiveAt(presence.getLastActiveAt())
                .build();
    }
}
