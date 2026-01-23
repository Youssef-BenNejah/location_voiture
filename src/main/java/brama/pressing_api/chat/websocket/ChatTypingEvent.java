package brama.pressing_api.chat.websocket;

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
public class ChatTypingEvent {
    private String conversationId;
    private String userId;
    private boolean typing;
    private LocalDateTime timestamp;
}
