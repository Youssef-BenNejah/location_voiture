package brama.pressing_api.chat.notification;

import brama.pressing_api.chat.domain.ChatMessage;
import brama.pressing_api.chat.domain.Conversation;
import org.springframework.stereotype.Service;

@Service
public interface PushNotificationService {
    void sendChatMessageNotification(String recipientId, Conversation conversation, ChatMessage message);
}
