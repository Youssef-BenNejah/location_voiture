package brama.pressing_api.notification.service;

import brama.pressing_api.notification.dto.NotificationRequest;

import java.util.Collection;

public interface NotificationService {
    void notifyUser(String userId, NotificationRequest request);

    void notifyUsers(Collection<String> userIds, NotificationRequest request);

    void notifyAdmins(NotificationRequest request);
}

