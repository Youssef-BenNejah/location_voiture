package brama.pressing_api.chat.dto.request;

import brama.pressing_api.chat.domain.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditChatMessageRequest {
    private String content;
    private ChatMessageType type;
    private List<ChatAttachmentRequest> attachments;
}
