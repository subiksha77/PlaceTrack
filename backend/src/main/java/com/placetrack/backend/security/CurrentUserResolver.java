package com.placetrack.backend.security;

import com.placetrack.backend.entity.User;
import com.placetrack.backend.exception.UnauthorizedException;
import com.placetrack.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Resolves the logged-in account from the DB-backed session token
 * sent in the X-Auth-Token header.
 */
@Component
@RequiredArgsConstructor
public class CurrentUserResolver {

    private final UserRepository userRepository;

    public User require(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Please log in to continue");
        }
        return userRepository.findByAuthToken(token.trim())
                .orElseThrow(() -> new UnauthorizedException("Session expired. Please log in again."));
    }
}
