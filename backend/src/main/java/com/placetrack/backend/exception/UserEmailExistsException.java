package com.placetrack.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a registration attempt uses an email that already has an account.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserEmailExistsException extends RuntimeException {

    public UserEmailExistsException(String email) {
        super("An account with email '" + email + "' already exists. Please login instead.");
    }
}
