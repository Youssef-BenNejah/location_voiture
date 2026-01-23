package brama.pressing_api.chat.domain;

import brama.pressing_api.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "chat_messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@CompoundIndexes({
        @CompoundIndex(name = "conversation_created_idx", def = "{'conversation_id': 1, 'created_date': -1}"),
        @CompoundIndex(name = "recipient_status_idx", def = "{'conversation_id': 1, 'recipient_id': 1, 'created_date': -1}")
})
public class ChatMessage extends BaseDocument {
    @Field("conversation_id")
    @Indexed
    private String conversationId;

    @Field("sender_id")
    @Indexed
    private String senderId;

    @Field("recipient_id")
    @Indexed
    private String recipientId;

    @Field("type")
    private ChatMessageType type;

    @Field("content")
    private String content;

    @Field("attachments")
    private List<ChatAttachment> attachments;

    @Field("receipts")
    private Map<String, ChatMessageReceipt> receipts;

    @Field("edited_at")
    private LocalDateTime editedAt;

    @Field("deleted")
    private boolean deleted;

    @Field("deleted_at")
    private LocalDateTime deletedAt;

    @Field("deleted_by")
    private String deletedBy;
}
