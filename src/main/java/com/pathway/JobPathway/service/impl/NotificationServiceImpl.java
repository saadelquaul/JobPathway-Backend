package com.pathway.JobPathway.service.impl;

import com.pathway.JobPathway.dto.NotificationResponse;
import com.pathway.JobPathway.entity.Notification;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.entity.enums.NotificationType;
import com.pathway.JobPathway.exception.ResourceNotFoundException;
import com.pathway.JobPathway.repository.NotificationRepository;
import com.pathway.JobPathway.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void createNotification(User recipient, NotificationType type, String title,
                                   String message, Long relatedEntityId, String relatedEntityType) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .title(title)
                .message(message)
                .relatedEntityId(relatedEntityId)
                .relatedEntityType(relatedEntityType)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public Page<NotificationResponse> getNotifications(User user, Pageable pageable) {
        return notificationRepository
                .findByRecipientIdOrderByIsReadAscCreatedAtDesc(user.getId(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    public long getUnreadCount(User user) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(user.getId());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(User user, Long notificationId) {
        Notification notification = getOwnedNotification(user, notificationId);
        notification.setIsRead(true);
        notificationRepository.save(notification);
        return mapToResponse(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unreadNotifications =
                notificationRepository.findByRecipientIdAndIsReadFalse(user.getId());
        unreadNotifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    @Transactional
    public void deleteNotification(User user, Long notificationId) {
        Notification notification = getOwnedNotification(user, notificationId);
        notificationRepository.delete(notification);
    }

    private Notification getOwnedNotification(User user, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id: " + notificationId));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new ResourceNotFoundException(
                    "Notification not found with id: " + notificationId);
        }

        return notification;
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .relatedEntityId(notification.getRelatedEntityId())
                .relatedEntityType(notification.getRelatedEntityType())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
