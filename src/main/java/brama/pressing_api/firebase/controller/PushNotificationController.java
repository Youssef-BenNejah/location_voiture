package brama.pressing_api.firebase.controller;

import brama.pressing_api.firebase.dto.request.PushNotificationRequest;
import brama.pressing_api.firebase.service.PushNotificationService;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/firebase-notifications")
@RequiredArgsConstructor
public class PushNotificationController {
    private final PushNotificationService notificationService;

    @PostMapping("/send-one")
    public ResponseEntity<String> sendToOne(@RequestBody PushNotificationRequest request) {
        return ResponseEntity.ok(notificationService.sendToOne(request));
    }

    @PostMapping("/send-many")
    public ResponseEntity<String> sendToMany(@RequestBody PushNotificationRequest request) {
        BatchResponse response = notificationService.sendToMany(request);
        return ResponseEntity.ok("Success: " + response.getSuccessCount()
                + " | Failed: " + response.getFailureCount());
    }

    @PostMapping("/send-topic")
    public ResponseEntity<String> sendToTopic(@RequestBody PushNotificationRequest request) {
        return ResponseEntity.ok(notificationService.sendToTopic(request));
    }
    @PostMapping("/subscribe-topic")
    public ResponseEntity<String> subscribeToTopic(@RequestParam String token, @RequestParam String topic) throws FirebaseMessagingException {
        TopicManagementResponse response = FirebaseMessaging.getInstance()
                .subscribeToTopic(Collections.singletonList(token), topic);
        return ResponseEntity.ok("Subscribed: " + response.getSuccessCount());
    }

    @PostMapping("/subscribe-user")
    public ResponseEntity<String> subscribeUserTopic(@RequestParam String token, @RequestParam String userId)
            throws FirebaseMessagingException {
        String topic = "user-" + userId;
        TopicManagementResponse response = FirebaseMessaging.getInstance()
                .subscribeToTopic(Collections.singletonList(token), topic);
        return ResponseEntity.ok("Subscribed to " + topic + ": " + response.getSuccessCount());
    }

    @PostMapping("/subscribe-user-many")
    public ResponseEntity<String> subscribeManyTokensToUserTopic(@RequestParam String userId,
                                                                 @RequestBody List<String> tokens)
            throws FirebaseMessagingException {
        String topic = "user-" + userId;
        TopicManagementResponse response = FirebaseMessaging.getInstance()
                .subscribeToTopic(tokens, topic);
        return ResponseEntity.ok("Subscribed to " + topic + ": " + response.getSuccessCount());
    }
}
