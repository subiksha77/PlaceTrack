package com.placetrack.backend.entity;

/**
 * Role of a registered account.
 * STUDENT - manages their own placement profile, applications and preparation.
 * ADMIN   - placement officer: manages students, companies, drives and applications.
 */
public enum UserRole {
    STUDENT,
    PLACEMENT_OFFICER,
    ADMIN
}
