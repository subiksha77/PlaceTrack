package com.placetrack.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an entity cannot be modified/deleted because other records
 * depend on it (e.g. deleting a student with existing placement records).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceInUseException extends RuntimeException {

    public ResourceInUseException(String message) {
        super(message);
    }
}