package com.placetrack.backend.service;

import com.placetrack.backend.dto.LoginRequest;
import com.placetrack.backend.dto.RegisterRequest;
import com.placetrack.backend.dto.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    UserResponse login(LoginRequest request);

    /** Invalidate the session token of the account it belongs to. */
    void logout(String token);
}
