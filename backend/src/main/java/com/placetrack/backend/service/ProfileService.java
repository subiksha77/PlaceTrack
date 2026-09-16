package com.placetrack.backend.service;

import com.placetrack.backend.dto.CertificationRequest;
import com.placetrack.backend.dto.InternshipRequest;
import com.placetrack.backend.dto.ProfileResponse;
import com.placetrack.backend.dto.ProfileUpdateRequest;
import com.placetrack.backend.dto.ProjectRequest;
import com.placetrack.backend.dto.ResumeDownload;
import com.placetrack.backend.dto.SkillRequest;
import com.placetrack.backend.entity.User;
import org.springframework.web.multipart.MultipartFile;

/**
 * Placement-profile features for the logged-in student account:
 * basics + education, skills, projects, certifications, internships and resume.
 * Students can only ever access their own profile (resolved from the session token).
 */
public interface ProfileService {

    ProfileResponse getMyProfile(User user);

    ProfileResponse updateMyProfile(User user, ProfileUpdateRequest request);

    // Skills
    ProfileResponse addSkill(User user, SkillRequest request);

    ProfileResponse updateSkill(User user, Long skillId, SkillRequest request);

    ProfileResponse deleteSkill(User user, Long skillId);

    // Projects
    ProfileResponse addProject(User user, ProjectRequest request);

    ProfileResponse updateProject(User user, Long projectId, ProjectRequest request);

    ProfileResponse deleteProject(User user, Long projectId);

    // Certifications
    ProfileResponse addCertification(User user, CertificationRequest request);

    ProfileResponse updateCertification(User user, Long certificationId, CertificationRequest request);

    ProfileResponse deleteCertification(User user, Long certificationId);

    // Internships
    ProfileResponse addInternship(User user, InternshipRequest request);

    ProfileResponse updateInternship(User user, Long internshipId, InternshipRequest request);

    ProfileResponse deleteInternship(User user, Long internshipId);

    // Resume
    ProfileResponse uploadResume(User user, MultipartFile file);

    ResumeDownload downloadResume(User user);

    ProfileResponse deleteResume(User user);
}
