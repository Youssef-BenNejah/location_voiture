package brama.pressing_api.chat.websocket;

import brama.pressing_api.chat.domain.UserPresence;
import brama.pressing_api.chat.service.UserPresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatPresenceEventListener {
    private final UserPresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleSessionConnected(final SessionConnectEvent event) {
        Principal principal = event.getUser();
        if (principal == null || principal.getName() == null) {
            return;
        }
        UserPresence presence = presenceService.markOnline(principal.getName());
        messagingTemplate.convertAndSend("/topic/presence", ChatPresenceEvent.builder()
                .userId(presence.getUserId())
                .online(true)
                .lastSeenAt(presence.getLastSeenAt())
                .lastActiveAt(presence.getLastActiveAt())
                .build());
    }

    @EventListener
    public void handleSessionDisconnected(final SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        if (principal == null || principal.getName() == null) {
            return;
        }
        UserPresence presence = presenceService.markOffline(principal.getName());
        messagingTemplate.convertAndSend("/topic/presence", ChatPresenceEvent.builder()
                .userId(presence.getUserId())
                .online(false)
                .lastSeenAt(presence.getLastSeenAt())
                .lastActiveAt(presence.getLastActiveAt())
                .build());
    }
}
