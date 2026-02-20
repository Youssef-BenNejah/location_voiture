package brama.pressing_api.notification.dto;

import brama.pressing_api.notification.domain.NotificationImportance;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;

@Value
@Builder
public class SocketNotificationPayload {
    String type;
    String title;
    String body;
    NotificationImportance importance;
    Map<String, String> data;
    LocalDateTime timestamp;
}

