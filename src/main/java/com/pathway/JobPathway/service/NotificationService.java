package com.pathway.JobPathway.service;

import com.pathway.JobPathway.dto.NotificationResponse;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.entity.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void createNotification(User recipient, NotificationType type, String title,
                            String message, Long relatedEntityId, String relatedEntityType);

    Page<NotificationResponse> getNotifications(User user, Pageable pageable);

    long getUnreadCount(User user);

    NotificationResponse markAsRead(User user, Long notificationId);

    void markAllAsRead(User user);

    void deleteNotification(User user, Long notificationId);
}
