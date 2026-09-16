package com.placetrack.backend.controller;

import com.placetrack.backend.dto.CertificationRequest;
import com.placetrack.backend.dto.InternshipRequest;
import com.placetrack.backend.dto.ProfileResponse;
import com.placetrack.backend.dto.ProfileUpdateRequest;
import com.placetrack.backend.dto.ProjectRequest;
import com.placetrack.backend.dto.ResumeDownload;
import com.placetrack.backend.dto.SkillRequest;
import com.placetrack.backend.entity.User;
import com.placetrack.backend.security.CurrentUserResolver;
import com.placetrack.backend.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Placement-profile endpoints for the logged-in account.
 * Every request is authenticated with the X-Auth-Token session header and
 * can only touch the caller's own profile.
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final CurrentUserResolver currentUser;

    // ===== Profile =====

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        return ResponseEntity.ok(profileService.getMyProfile(currentUser.require(token)));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(profileService.updateMyProfile(currentUser.require(token), request));
    }

    // ===== Resume =====

    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileResponse> uploadResume(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(profileService.uploadResume(currentUser.require(token), file));
    }

    @GetMapping("/resume")
    public ResponseEntity<Resource> downloadResume(
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        ResumeDownload download = profileService.downloadResume(currentUser.require(token));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        download.contentType() == null ? "application/octet-stream" : download.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + download.fileName().replace("\"", "'") + "\"")
                .body(download.resource());
    }

    @DeleteMapping("/resume")
    public ResponseEntity<ProfileResponse> deleteResume(
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        return ResponseEntity.ok(profileService.deleteResume(currentUser.require(token)));
    }

    // ===== Skills =====

    @PostMapping("/skills")
    public ResponseEntity<ProfileResponse> addSkill(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @Valid @RequestBody SkillRequest request) {
        return ResponseEntity.ok(profileService.addSkill(currentUser.require(token), request));
    }

    @PutMapping("/skills/{id}")
    public ResponseEntity<ProfileResponse> updateSkill(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @PathVariable Long id, @Valid @RequestBody SkillRequest request) {
        return ResponseEntity.ok(profileService.updateSkill(currentUser.require(token), id, request));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<ProfileResponse> deleteSkill(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @PathVariable Long id) {
        return ResponseEntity.ok(profileService.deleteSkill(currentUser.require(token), id));
    }

    // ===== Projects =====

    @PostMapping("/projects")
    public ResponseEntity<ProfileResponse> addProject(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(profileService.addProject(currentUser.require(token), request));
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ProfileResponse> updateProject(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(profileService.updateProject(currentUser.require(token), id, request));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<ProfileResponse> deleteProject(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @PathVariable Long id) {
        return ResponseEntity.ok(profileService.deleteProject(currentUser.require(token), id));
    }

    // ===== Certifications =====

    @PostMapping("/certifications")
    public ResponseEntity<ProfileResponse> addCertification(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @Valid @RequestBody CertificationRequest request) {
        return ResponseEntity.ok(profileService.addCertification(currentUser.require(token), request));
    }

    @PutMapping("/certifications/{id}")
    public ResponseEntity<ProfileResponse> updateCertification(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @PathVariable Long id, @Valid @RequestBody CertificationRequest request) {
        return ResponseEntity.ok(profileService.updateCertification(currentUser.require(token), id, request));
    }

    @DeleteMapping("/certifications/{id}")
    public ResponseEntity<ProfileResponse> deleteCertification(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @PathVariable Long id) {
        return ResponseEntity.ok(profileService.deleteCertification(currentUser.require(token), id));
    }

    // ===== Internships =====

    @PostMapping("/internships")
    public ResponseEntity<ProfileResponse> addInternship(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @Valid @RequestBody InternshipRequest request) {
        return ResponseEntity.ok(profileService.addInternship(currentUser.require(token), request));
    }

    @PutMapping("/internships/{id}")
    public ResponseEntity<ProfileResponse> updateInternship(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @PathVariable Long id, @Valid @RequestBody InternshipRequest request) {
        return ResponseEntity.ok(profileService.updateInternship(currentUser.require(token), id, request));
    }

    @DeleteMapping("/internships/{id}")
    public ResponseEntity<ProfileResponse> deleteInternship(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @PathVariable Long id) {
        return ResponseEntity.ok(profileService.deleteInternship(currentUser.require(token), id));
    }
}
