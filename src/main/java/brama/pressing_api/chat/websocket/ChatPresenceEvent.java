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
public class ChatPresenceEvent {
    private String userId;
    private boolean online;
    private LocalDateTime lastSeenAt;
    private LocalDateTime lastActiveAt;
}
