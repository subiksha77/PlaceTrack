package com.placetrack.backend.dto;

import com.placetrack.backend.entity.User;
import com.placetrack.backend.entity.UserRole;

/**
 * Safe view of an account. Password hashes are never exposed.
 * The session token is included so the frontend can authenticate later requests.
 */
public record UserResponse(
        Long id,
        String fullName,
        String email,
        String department,
        Integer year,
        String role,
        String token
) {
    public static UserResponse from(User user) {
        return from(user, null);
    }

    public static UserResponse from(User user, String token) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getDepartment(),
                user.getYear(),
                user.getRole() == null ? UserRole.STUDENT.name() : user.getRole().name(),
                token
        );
    }
}
