package brama.pressing_api.notification.domain.model;

import brama.pressing_api.common.BaseDocument;
import brama.pressing_api.notification.domain.NotificationImportance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Notification extends BaseDocument {
    @Field("user_id")
    private String userId;

    @Field("type")
    private String type;

    @Field("title")
    private String title;

    @Field("body")
    private String body;

    @Field("importance")
    private NotificationImportance importance;

    @Field("data")
    private Map<String, String> data;

    @Field("read")
    private boolean read;

    @Field("read_at")
    private LocalDateTime readAt;
}

