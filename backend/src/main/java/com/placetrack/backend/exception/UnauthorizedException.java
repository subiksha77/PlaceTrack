package com.placetrack.backend.exception;

/** Thrown when a request has no valid session token (HTTP 401). */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Authentication required. Please log in.");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
