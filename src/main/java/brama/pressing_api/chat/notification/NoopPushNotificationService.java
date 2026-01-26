package brama.pressing_api.chat.notification;

import brama.pressing_api.chat.domain.ChatMessage;
import brama.pressing_api.chat.domain.Conversation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service

@Slf4j
public class NoopPushNotificationService implements PushNotificationService {
    @Override
    public void sendChatMessageNotification(final String recipientId,
                                            final Conversation conversation,
                                            final ChatMessage message) {
        log.debug("Push notification skipped for recipient {} (no provider configured)", recipientId);
    }
}
