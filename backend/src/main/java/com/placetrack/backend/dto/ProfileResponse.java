package com.placetrack.backend.dto;

import java.util.List;

/**
 * Complete placement profile of the logged-in student, including the
 * transparent profile-completeness checklist computed from actual data.
 */
public record ProfileResponse(
        Long id,
        Long userId,
        String studentName,
        String email,
        String phone,
        String department,
        Integer year,
        Double cgpa,
        Integer graduationYear,
        Double tenthPercentage,
        Double twelfthPercentage,
        Integer backlogs,
        String registerNumber,
        String degree,
        String dateOfBirth,
        String gender,
        String location,
        String address,
        String city,
        String state,
        String linkedIn,
        String gitHub,
        String codingProfiles,
        String achievements,
        String skills,
        String placementStatus,
        ResumeInfo resume,
        List<SkillItem> skillList,
        List<ProjectItem> projects,
        List<CertificationItem> certifications,
        List<InternshipItem> internships,
        Integer completenessPercent,
        List<ChecklistItem> completeness
) {

    /** Resume metadata - the raw storage path is never exposed. */
    public record ResumeInfo(String fileName, String updatedAt, boolean hasResume) {}

    public record SkillItem(Long id, String skillName, String category) {}

    public record ProjectItem(Long id, String projectName, String description,
                              String technologies, String projectLink, String projectRole) {}

    public record CertificationItem(Long id, String certificationName, String issuingOrganization,
                                    String issueDate, String credentialLink) {}

    public record InternshipItem(Long id, String companyName, String role,
                                 String duration, String description) {}

    /** One transparent completeness rule: label + whether it is satisfied + its weight. */
    public record ChecklistItem(String label, boolean done, int weight) {}
}
