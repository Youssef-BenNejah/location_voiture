package brama.pressing_api.notification.dto;

import brama.pressing_api.notification.domain.NotificationChannel;
import brama.pressing_api.notification.domain.NotificationImportance;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class NotificationRequest {
    String type;
    String title;
    String body;
    Map<String, String> data;
    NotificationImportance importance;
    NotificationChannel channel;
}

