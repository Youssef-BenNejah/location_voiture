package brama.pressing_api.chat.domain;

import brama.pressing_api.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "conversations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Conversation extends BaseDocument {
    @Field("participant_ids")
    @Indexed
    private List<String> participantIds;

    @Field("last_message_id")
    private String lastMessageId;

    @Field("last_message_sender_id")
    private String lastMessageSenderId;

    @Field("last_message_preview")
    private String lastMessagePreview;

    @Field("last_message_at")
    private LocalDateTime lastMessageAt;

    @Field("unread_counts")
    private Map<String, Integer> unreadCounts;
}
