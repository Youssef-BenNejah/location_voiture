package brama.pressing_api.firebase.service;

import brama.pressing_api.firebase.dto.request.PushNotificationRequest;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationService {
    private final FirebaseMessaging firebaseMessaging;

    /**
     * Send to a single user by FCM token
     */
    public String sendToOne(PushNotificationRequest request) {
        try {
            Message message = Message.builder()
                    .setToken(request.getToken())
                    .setNotification(buildNotification(request))
                    .putAllData(request.getData() != null ? request.getData() : Map.of())
                    .build();

            String response = firebaseMessaging.send(message);
            log.info("Notification sent to token [{}]: {}", request.getToken(), response);
            return response;

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send notification to token [{}]: {}", request.getToken(), e.getMessage());
            throw new RuntimeException("Failed to send push notification", e);
        }
    }

    /**
     * Send to multiple users by FCM tokens (batch)
     */
    public BatchResponse sendToMany(PushNotificationRequest request) {
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(request.getTokens())
                    .setNotification(buildNotification(request))
                    .putAllData(request.getData() != null ? request.getData() : Map.of())
                    .build();

            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
            log.info("Sent to {}/{} devices successfully",
                    response.getSuccessCount(), request.getTokens().size());

            // Log failed tokens
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        log.warn("Failed token [{}]: {}", request.getTokens().get(i),
                                responses.get(i).getException().getMessage());
                    }
                }
            }
            return response;

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send multicast notification: {}", e.getMessage());
            throw new RuntimeException("Failed to send multicast push notification", e);
        }
    }

    /**
     * Send to a topic (broadcast to all subscribers)
     */
    public String sendToTopic(PushNotificationRequest request) {
        try {
            Message message = Message.builder()
                    .setTopic(request.getTopic())
                    .setNotification(buildNotification(request))
                    .putAllData(request.getData() != null ? request.getData() : Map.of())
                    .build();

            String response = firebaseMessaging.send(message);
            log.info("Notification sent to topic [{}]: {}", request.getTopic(), response);
            return response;

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send notification to topic [{}]: {}", request.getTopic(), e.getMessage());
            throw new RuntimeException("Failed to send topic notification", e);
        }
    }

    /**
     * Subscribe tokens to a topic
     */
    public void subscribeToTopic(List<String> tokens, String topic) {
        try {
            TopicManagementResponse response = firebaseMessaging.subscribeToTopic(tokens, topic);
            log.info("Subscribed {}/{} tokens to topic [{}]",
                    response.getSuccessCount(), tokens.size(), topic);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("Failed to subscribe to topic", e);
        }
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private Notification buildNotification(PushNotificationRequest request) {
        return Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .build();
    }
}
