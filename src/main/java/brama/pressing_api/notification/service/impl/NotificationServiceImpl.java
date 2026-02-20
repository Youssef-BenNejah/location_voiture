package brama.pressing_api.notification.service.impl;

import brama.pressing_api.firebase.dto.request.PushNotificationRequest;
import brama.pressing_api.notification.domain.NotificationChannel;
import brama.pressing_api.notification.domain.NotificationImportance;
import brama.pressing_api.notification.dto.NotificationRequest;
import brama.pressing_api.notification.dto.SocketNotificationPayload;
import brama.pressing_api.notification.service.NotificationService;
import brama.pressing_api.user.User;
import brama.pressing_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        List<User> admins = userRepository.findByRolesContaining("ADMIN");
        List<User> prefixedAdmins = userRepository.findByRolesContaining("ROLE_ADMIN");
        Set<String> adminIds = new LinkedHashSet<>();

        admins.stream().map(User::getId).forEach(adminIds::add);
        prefixedAdmins.stream().map(User::getId).forEach(adminIds::add);

        notifyUsers(adminIds, request);
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
        Map<String, String> data = request.getData() != null ? request.getData() : Map.of();
        for (String userId : recipients) {
            try {
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

    private String userTopic(final String userId) {
        return USER_TOPIC_PREFIX + userId;
    }
}

