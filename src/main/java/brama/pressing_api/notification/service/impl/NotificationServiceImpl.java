package brama.pressing_api.notification.service.impl;

import brama.pressing_api.firebase.dto.request.PushNotificationRequest;
import brama.pressing_api.notification.domain.NotificationChannel;
import brama.pressing_api.notification.domain.NotificationImportance;
import brama.pressing_api.notification.dto.NotificationRequest;
import brama.pressing_api.notification.dto.NotificationResponse;
import brama.pressing_api.notification.dto.SocketNotificationPayload;
import brama.pressing_api.notification.domain.model.Notification;
import brama.pressing_api.notification.repo.NotificationRepository;
import brama.pressing_api.notification.service.NotificationService;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.user.User;
import brama.pressing_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private static final String USER_QUEUE = "/queue/notifications";
    private static final String USER_TOPIC_PREFIX = "user-";

    private final SimpMessagingTemplate messagingTemplate;
    private final brama.pressing_api.firebase.service.PushNotificationService pushNotificationService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public void notifyUser(final String userId, final NotificationRequest request) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        notifyUsers(List.of(userId), request);
    }

    @Override
    public void notifyUsers(final Collection<String> userIds, final NotificationRequest request) {
        if (userIds == null || userIds.isEmpty() || request == null) {
            return;
        }

        Set<String> recipients = userIds.stream()
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (recipients.isEmpty()) {
            return;
        }

        persistNotifications(recipients, request);

        NotificationChannel channel = resolveChannel(request);
        if (channel == NotificationChannel.SOCKET || channel == NotificationChannel.BOTH) {
            sendSocketNotifications(recipients, request);
        }
        if (channel == NotificationChannel.PUSH || channel == NotificationChannel.BOTH) {
            sendPushNotifications(recipients, request);
        }
    }

    @Override
    public void notifyAdmins(final NotificationRequest request) {
        Set<String> adminIds = resolveAdminIds();
        notifyUsers(adminIds, request);
    }

    @Override
    public Page<NotificationResponse> listMyNotifications(final String userId, final Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Override
    public long countUnread(final String userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public NotificationResponse markAsRead(final String userId, final String notificationId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);
        }
        return toResponse(notification);
    }

    @Override
    public int markAllAsRead(final String userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(userId);
        if (unread.isEmpty()) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        for (Notification notification : unread) {
            notification.setRead(true);
            notification.setReadAt(now);
        }
        notificationRepository.saveAll(unread);
        return unread.size();
    }

    private void sendSocketNotifications(final Set<String> recipients, final NotificationRequest request) {
        SocketNotificationPayload payload = SocketNotificationPayload.builder()
                .type(request.getType())
                .title(request.getTitle())
                .body(request.getBody())
                .importance(resolveImportance(request))
                .data(request.getData())
                .timestamp(LocalDateTime.now())
                .build();

        for (String userId : recipients) {
            try {
                messagingTemplate.convertAndSendToUser(userId, USER_QUEUE, payload);
            } catch (RuntimeException ex) {
                log.warn("Socket notification failed for user {}", userId, ex);
            }
        }
    }

    private void sendPushNotifications(final Set<String> recipients, final NotificationRequest request) {
        Set<String> adminIds = resolveAdminIds();
        if (adminIds.isEmpty()) {
            return;
        }
        Set<String> pushRecipients = recipients.stream()
                .filter(adminIds::contains)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (pushRecipients.isEmpty()) {
            return;
        }
        Map<String, String> data = request.getData() != null ? request.getData() : Map.of();
        for (String userId : pushRecipients) {
            try {
                log.info("Sending push notification to user {} (topic {}) type={}", userId, userTopic(userId), request.getType());
                pushNotificationService.sendToTopic(PushNotificationRequest.builder()
                        .title(request.getTitle())
                        .body(request.getBody())
                        .data(data)
                        .topic(userTopic(userId))
                        .build());
            } catch (RuntimeException ex) {
                log.warn("Push notification failed for user {}", userId, ex);
            }
        }
    }

    private NotificationChannel resolveChannel(final NotificationRequest request) {
        NotificationChannel requested = request.getChannel() != null ? request.getChannel() : NotificationChannel.AUTO;
        if (requested != NotificationChannel.AUTO) {
            return requested;
        }

        NotificationImportance importance = resolveImportance(request);
        if (importance == NotificationImportance.HIGH || importance == NotificationImportance.CRITICAL) {
            return NotificationChannel.BOTH;
        }
        return NotificationChannel.SOCKET;
    }

    private NotificationImportance resolveImportance(final NotificationRequest request) {
        return request.getImportance() != null ? request.getImportance() : NotificationImportance.NORMAL;
    }

    private void persistNotifications(final Set<String> recipients, final NotificationRequest request) {
        NotificationImportance importance = resolveImportance(request);
        Map<String, String> data = request.getData() != null ? request.getData() : Map.of();
        List<Notification> notifications = new ArrayList<>();
        for (String userId : recipients) {
            notifications.add(Notification.builder()
                    .userId(userId)
                    .type(request.getType())
                    .title(request.getTitle())
                    .body(request.getBody())
                    .importance(importance)
                    .data(data)
                    .read(false)
                    .build());
        }
        notificationRepository.saveAll(notifications);
    }

    private NotificationResponse toResponse(final Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .body(notification.getBody())
                .importance(notification.getImportance())
                .data(notification.getData())
                .read(notification.isRead())
                .readAt(notification.getReadAt())
                .createdDate(notification.getCreatedDate())
                .build();
    }

    private String userTopic(final String userId) {
        return USER_TOPIC_PREFIX + userId;
    }

    private Set<String> resolveAdminIds() {
        List<User> admins = userRepository.findByRolesContaining("ADMIN");
        List<User> prefixedAdmins = userRepository.findByRolesContaining("ROLE_ADMIN");
        Set<String> adminIds = new LinkedHashSet<>();
        admins.stream().map(User::getId).forEach(adminIds::add);
        prefixedAdmins.stream().map(User::getId).forEach(adminIds::add);
        return adminIds;
    }
}
