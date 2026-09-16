package com.placetrack.backend.service.impl;

import com.placetrack.backend.dto.LoginRequest;
import com.placetrack.backend.dto.RegisterRequest;
import com.placetrack.backend.dto.UserResponse;
import com.placetrack.backend.entity.Student;
import com.placetrack.backend.entity.User;
import com.placetrack.backend.entity.UserRole;
import com.placetrack.backend.exception.InvalidCredentialsException;
import com.placetrack.backend.exception.UserEmailExistsException;
import com.placetrack.backend.repository.StudentRepository;
import com.placetrack.backend.repository.UserRepository;
import com.placetrack.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new UserEmailExistsException(email);
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        // Only the BCrypt hash is stored - never the raw password
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDepartment(request.department().trim());
        user.setYear(request.year());
        if ("ADMIN".equalsIgnoreCase(request.role())) {
            user.setRole(UserRole.ADMIN);
        } else if ("PLACEMENT_OFFICER".equalsIgnoreCase(request.role()) || "OFFICER".equalsIgnoreCase(request.role())) {
            user.setRole(UserRole.PLACEMENT_OFFICER);
        } else {
            user.setRole(UserRole.STUDENT);
        }

        User saved = userRepository.save(user);

        // Every student account gets a real placement profile row in `students`,
        // linked by user_id. If the placement office already created a student
        // record with the same email, link to it instead of duplicating.
        if (saved.getRole() == UserRole.STUDENT) {
            Student profile = studentRepository.findByEmail(email).orElseGet(Student::new);
            profile.setStudentName(saved.getFullName());
            profile.setEmail(email);
            profile.setDepartment(saved.getDepartment());
            profile.setYear(saved.getYear());
            if (profile.getCgpa() == null) {
                profile.setCgpa(0.0);  // DB column is NOT NULL; student updates it from their profile
            }
            if (profile.getPhone() == null) {
                profile.setPhone("");   // DB column is NOT NULL; student fills this in from profile
            }
            if (profile.getPlacementStatus() == null) {
                profile.setPlacementStatus(Student.PlacementStatus.NOT_PLACED);
            }
            profile.setUserId(saved.getId());
            studentRepository.save(profile);
        }

        return UserResponse.from(saved, issueToken(saved));
    }

    @Override
    @Transactional
    public UserResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // DB-backed session token so protected endpoints can identify the caller
        return UserResponse.from(user, issueToken(user));
    }

    @Override
    @Transactional
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        userRepository.findByAuthToken(token).ifPresent(user -> user.setAuthToken(null));
    }

    private String issueToken(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        user.setAuthToken(token);
        return userRepository.save(user).getAuthToken();
    }
}

