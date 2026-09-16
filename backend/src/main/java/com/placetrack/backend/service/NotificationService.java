package com.placetrack.backend.service;

import com.placetrack.backend.dto.NotificationRequest;
import com.placetrack.backend.dto.NotificationResponse;
import com.placetrack.backend.entity.User;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getForUser(User user);
    long getUnreadCount(User user);
    NotificationResponse markRead(Long id);
    void markAllRead(User user);
    NotificationResponse create(NotificationRequest request);
}
