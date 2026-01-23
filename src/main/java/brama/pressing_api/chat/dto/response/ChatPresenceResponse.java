package brama.pressing_api.chat.dto.response;

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
public class ChatPresenceResponse {
    private boolean online;
    private LocalDateTime lastSeenAt;
    private LocalDateTime lastActiveAt;
}
