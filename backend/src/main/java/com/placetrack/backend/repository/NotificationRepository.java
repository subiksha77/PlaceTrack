package com.placetrack.backend.repository;

import com.placetrack.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE (n.userId IS NULL OR n.userId = :userId) " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE (n.userId IS NULL OR n.userId = :userId) " +
            "AND n.read = false")
    long countUnreadForUser(@Param("userId") Long userId);
}
