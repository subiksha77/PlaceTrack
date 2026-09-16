package com.placetrack.backend.service.impl;

import com.placetrack.backend.dto.NotificationRequest;
import com.placetrack.backend.dto.NotificationResponse;
import com.placetrack.backend.entity.Notification;
import com.placetrack.backend.entity.User;
import com.placetrack.backend.exception.ResourceNotFoundException;
import com.placetrack.backend.repository.NotificationRepository;
import com.placetrack.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationResponse> getForUser(User user) {
        return notificationRepository.findForUser(user.getId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    public long getUnreadCount(User user) {
        return notificationRepository.countUnreadForUser(user.getId());
    }

    @Override
    @Transactional
    public NotificationResponse markRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        n.setRead(true);
        return toResponse(notificationRepository.save(n));
    }

    @Override
    @Transactional
    public void markAllRead(User user) {
        List<Notification> notifications = notificationRepository.findForUser(user.getId());
        for (Notification n : notifications) {
            if (!n.isRead()) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        }
    }

    @Override
    @Transactional
    public NotificationResponse create(NotificationRequest request) {
        Notification n = new Notification();
        n.setUserId(request.userId());
        n.setTitle(request.title());
        n.setMessage(request.message());
        n.setType(request.type() != null
                ? Notification.NotificationType.valueOf(request.type().toUpperCase())
                : Notification.NotificationType.INFO);
        n.setLink(request.link());
        n.setRead(false);
        n.setCreatedAt(LocalDateTime.now());
        return toResponse(notificationRepository.save(n));
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getUserId(),
                n.getTitle(),
                n.getMessage(),
                n.getType().name(),
                n.getLink(),
                n.isRead(),
                n.getCreatedAt() != null ? n.getCreatedAt().toString() : null
        );
    }
}
