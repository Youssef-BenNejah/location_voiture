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
public class ChatConversationResponse {
    private String id;
    private ChatUserSummary participant;
    private ChatPresenceResponse presence;
    private String lastMessageId;
    private String lastMessageSenderId;
    private String lastMessagePreview;
    private LocalDateTime lastMessageAt;
    private int unreadCount;
}
