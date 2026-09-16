package com.placetrack.backend.controller;

import com.placetrack.backend.dto.NotificationRequest;
import com.placetrack.backend.dto.NotificationResponse;
import com.placetrack.backend.entity.User;
import com.placetrack.backend.security.CurrentUserResolver;
import com.placetrack.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        User user = currentUserResolver.require(token);
        return ResponseEntity.ok(notificationService.getForUser(user));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        User user = currentUserResolver.require(token);
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(user)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markRead(id));
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllRead(
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        User user = currentUserResolver.require(token);
        notificationService.markAllRead(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.create(request));
    }
}
