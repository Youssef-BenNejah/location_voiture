package brama.pressing_api.notification;

import brama.pressing_api.notification.dto.NotificationResponse;
import brama.pressing_api.notification.dto.UnreadCountResponse;
import brama.pressing_api.notification.service.NotificationService;
import brama.pressing_api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public Page<NotificationResponse> listMine(final Pageable pageable, final Authentication authentication) {
        return notificationService.listMyNotifications(currentUserId(authentication), pageable);
    }

    @GetMapping("/unread-count")
    public UnreadCountResponse unreadCount(final Authentication authentication) {
        long unread = notificationService.countUnread(currentUserId(authentication));
        return new UnreadCountResponse(unread);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markRead(@PathVariable("id") final String id,
                                         final Authentication authentication) {
        return notificationService.markAsRead(currentUserId(authentication), id);
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Integer>> markAllRead(final Authentication authentication) {
        int updated = notificationService.markAllAsRead(currentUserId(authentication));
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("updated", updated));
    }

    private String currentUserId(final Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }
}

