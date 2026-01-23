package brama.pressing_api.chat.service;

import brama.pressing_api.chat.dto.request.CreateConversationRequest;
import brama.pressing_api.chat.dto.request.EditChatMessageRequest;
import brama.pressing_api.chat.dto.request.SendChatMessageRequest;
import brama.pressing_api.chat.dto.response.ChatConversationResponse;
import brama.pressing_api.chat.dto.response.ChatMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {
    ChatConversationResponse createConversation(CreateConversationRequest request, String userId);

    List<ChatConversationResponse> listConversations(String userId);

    Page<ChatMessageResponse> listMessages(String conversationId, Pageable pageable, String userId);

    ChatMessageResponse sendMessage(SendChatMessageRequest request, String senderId);

    ChatMessageResponse editMessage(String messageId, EditChatMessageRequest request, String userId);

    ChatMessageResponse deleteMessage(String messageId, String userId);

    void markConversationRead(String conversationId, String userId);

    void markConversationDelivered(String conversationId, String userId);

    void sendTyping(String conversationId, String userId, boolean typing);
}
