package brama.pressing_api.chat.websocket;

import brama.pressing_api.chat.domain.ChatMessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatReceiptEvent {
    private String conversationId;
    private String userId;
    private ChatMessageStatus status;
    private LocalDateTime timestamp;
}
