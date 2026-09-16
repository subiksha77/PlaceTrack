package com.placetrack.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * BCrypt hash of the password. Never returned by any API response -
     * auth endpoints expose {@link com.placetrack.backend.dto.UserResponse} only.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotBlank(message = "Department is required")
    @Column(name = "department", nullable = false)
    private String department;

    @NotNull(message = "Year is required")
    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 4, message = "Year must be at most 4")
    @Column(name = "year", nullable = false)
    private Integer year;

    /** STUDENT accounts get a linked placement profile; ADMIN accounts manage the platform. */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role = UserRole.STUDENT;

    /**
     * DB-backed session token issued on login/register and cleared on logout.
     * Lets the backend identify the caller for "my profile" endpoints.
     */
    @Column(name = "auth_token", length = 64)
    private String authToken;
}

