package brama.pressing_api.chat.dto.response;

import brama.pressing_api.chat.domain.ChatAttachment;
import brama.pressing_api.chat.domain.ChatMessageReceipt;
import brama.pressing_api.chat.domain.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String id;
    private String conversationId;
    private String senderId;
    private String recipientId;
    private ChatMessageType type;
    private String content;
    private List<ChatAttachment> attachments;
    private Map<String, ChatMessageReceipt> receipts;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private LocalDateTime editedAt;
    private boolean deleted;
    private LocalDateTime deletedAt;
    private String deletedBy;
}
