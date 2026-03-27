package com.pathway.JobPathway.controller;

import com.pathway.JobPathway.dto.CountResponse;
import com.pathway.JobPathway.dto.MessageResponse;
import com.pathway.JobPathway.dto.NotificationResponse;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get notifications", description = "Retrieve all notifications for the authenticated user")
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(hidden = true) @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotifications(user, pageable));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread count", description = "Get the count of unread notifications")
    public ResponseEntity<CountResponse> getUnreadCount(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        long count = notificationService.getUnreadCount(user);
        return ResponseEntity.ok(new CountResponse(count));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark as read", description = "Mark a specific notification as read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(user, id));
    }

    @PutMapping("/mark-all-read")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read")
    public ResponseEntity<MessageResponse> markAllAsRead(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok(new MessageResponse("All notifications marked as read"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Delete a specific notification")
    public ResponseEntity<Void> deleteNotification(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        notificationService.deleteNotification(user, id);
        return ResponseEntity.noContent().build();
    }
}
