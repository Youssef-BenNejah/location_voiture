package brama.pressing_api.notification.service;

import brama.pressing_api.notification.dto.NotificationRequest;
import brama.pressing_api.notification.dto.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface NotificationService {
    void notifyUser(String userId, NotificationRequest request);

    void notifyUsers(Collection<String> userIds, NotificationRequest request);

    void notifyAdmins(NotificationRequest request);

    Page<NotificationResponse> listMyNotifications(String userId, Pageable pageable);

    long countUnread(String userId);

    NotificationResponse markAsRead(String userId, String notificationId);

    int markAllAsRead(String userId);
}
