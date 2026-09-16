package com.placetrack.backend.service.impl;

import com.placetrack.backend.dto.CertificationRequest;
import com.placetrack.backend.dto.InternshipRequest;
import com.placetrack.backend.dto.ProfileResponse;
import com.placetrack.backend.dto.ProfileUpdateRequest;
import com.placetrack.backend.dto.ProjectRequest;
import com.placetrack.backend.dto.ResumeDownload;
import com.placetrack.backend.dto.SkillRequest;
import com.placetrack.backend.entity.Student;
import com.placetrack.backend.entity.StudentCertification;
import com.placetrack.backend.entity.StudentInternship;
import com.placetrack.backend.entity.StudentProject;
import com.placetrack.backend.entity.StudentSkill;
import com.placetrack.backend.entity.User;
import com.placetrack.backend.entity.UserRole;
import com.placetrack.backend.exception.ResourceNotFoundException;
import com.placetrack.backend.exception.UnauthorizedException;
import com.placetrack.backend.repository.StudentCertificationRepository;
import com.placetrack.backend.repository.StudentInternshipRepository;
import com.placetrack.backend.repository.StudentProjectRepository;
import com.placetrack.backend.repository.StudentRepository;
import com.placetrack.backend.repository.StudentSkillRepository;
import com.placetrack.backend.repository.UserRepository;
import com.placetrack.backend.service.ProfileService;
import com.placetrack.backend.service.ResumeStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StudentSkillRepository skillRepository;
    private final StudentProjectRepository projectRepository;
    private final StudentCertificationRepository certificationRepository;
    private final StudentInternshipRepository internshipRepository;
    private final ResumeStorageService resumeStorage;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ===== Own profile =====

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(User user) {
        return toResponse(requireProfile(user));
    }

    @Override
    @Transactional
    public ProfileResponse updateMyProfile(User user, ProfileUpdateRequest request) {
        Student student = requireProfile(user);
        student.setStudentName(request.studentName().trim());
        student.setPhone(blankToNull(request.phone()));
        student.setSkills(blankToNull(request.skills()));
        student.setCgpa(request.cgpa());
        student.setGraduationYear(request.graduationYear());
        student.setTenthPercentage(request.tenthPercentage());
        student.setTwelfthPercentage(request.twelfthPercentage());
        student.setBacklogs(request.backlogs());
        student.setRegisterNumber(blankToNull(request.registerNumber()));
        student.setDegree(blankToNull(request.degree()));
        student.setDateOfBirth(request.dateOfBirth());
        student.setGender(blankToNull(request.gender()) == null ? null : request.gender().trim().toUpperCase());
        student.setLocation(blankToNull(request.location()));
        student.setAddress(blankToNull(request.address()));
        student.setCity(blankToNull(request.city()));
        student.setState(blankToNull(request.state()));
        student.setLinkedIn(blankToNull(request.linkedIn()));
        student.setGitHub(blankToNull(request.gitHub()));
        student.setCodingProfiles(blankToNull(request.codingProfiles()));
        student.setAchievements(blankToNull(request.achievements()));

        // Keep the login account's display name in sync with the profile
        if (!user.getFullName().equals(student.getStudentName())) {
            user.setFullName(student.getStudentName());
            userRepository.save(user);
        }
        return toResponse(studentRepository.save(student));
    }

    // ===== Skills =====

    @Override
    @Transactional
    public ProfileResponse addSkill(User user, SkillRequest request) {
        Student student = requireProfile(user);
        StudentSkill skill = new StudentSkill();
        skill.setStudent(student);
        applySkill(skill, request);
        skillRepository.save(skill);
        return toResponse(student);
    }

    @Override
    @Transactional
    public ProfileResponse updateSkill(User user, Long skillId, SkillRequest request) {
        Student student = requireProfile(user);
        StudentSkill skill = skillRepository.findByIdAndStudentId(skillId, student.getId())
                .orElseThrow(() -> notFound("Skill"));
        applySkill(skill, request);
        skillRepository.save(skill);
        return toResponse(student);
    }

    @Override
    @Transactional
    public ProfileResponse deleteSkill(User user, Long skillId) {
        Student student = requireProfile(user);
        StudentSkill skill = skillRepository.findByIdAndStudentId(skillId, student.getId())
                .orElseThrow(() -> notFound("Skill"));
        skillRepository.delete(skill);
        return toResponse(student);
    }

    private void applySkill(StudentSkill skill, SkillRequest request) {
        skill.setName(request.skillName().trim());
        skill.setCategory(request.category() == null || request.category().isBlank()
                ? StudentSkill.SkillCategory.OTHER
                : StudentSkill.SkillCategory.valueOf(request.category().toUpperCase()));
    }

    // ===== Projects =====

    @Override
    @Transactional
    public ProfileResponse addProject(User user, ProjectRequest request) {
        Student student = requireProfile(user);
        StudentProject project = new StudentProject();
        project.setStudent(student);
        applyProject(project, request);
        projectRepository.save(project);
        return toResponse(student);
    }

    @Override
    @Transactional
    public ProfileResponse updateProject(User user, Long projectId, ProjectRequest request) {
        Student student = requireProfile(user);
        StudentProject project = projectRepository.findByIdAndStudentId(projectId, student.getId())
                .orElseThrow(() -> notFound("Project"));
        applyProject(project, request);
        projectRepository.save(project);
        return toResponse(student);
    }

    @Override
    @Transactional
    public ProfileResponse deleteProject(User user, Long projectId) {
        Student student = requireProfile(user);
        StudentProject project = projectRepository.findByIdAndStudentId(projectId, student.getId())
                .orElseThrow(() -> notFound("Project"));
        projectRepository.delete(project);
        return toResponse(student);
    }

    private void applyProject(StudentProject project, ProjectRequest request) {
        project.setName(request.projectName().trim());
        project.setDescription(blankToNull(request.description()));
        project.setTechnologies(blankToNull(request.technologies()));
        project.setProjectLink(blankToNull(request.projectLink()));
    }

    // ===== Certifications =====

    @Override
    @Transactional
    public ProfileResponse addCertification(User user, CertificationRequest request) {
        Student student = requireProfile(user);
        StudentCertification cert = new StudentCertification();
        cert.setStudent(student);
        applyCertification(cert, request);
        certificationRepository.save(cert);
        return toResponse(student);
    }

    @Override
    @Transactional
    public ProfileResponse updateCertification(User user, Long certificationId, CertificationRequest request) {
        Student student = requireProfile(user);
        StudentCertification cert = certificationRepository.findByIdAndStudentId(certificationId, student.getId())
                .orElseThrow(() -> notFound("Certification"));
        applyCertification(cert, request);
        certificationRepository.save(cert);
        return toResponse(student);
    }

    @Override
    @Transactional
    public ProfileResponse deleteCertification(User user, Long certificationId) {
        Student student = requireProfile(user);
        StudentCertification cert = certificationRepository.findByIdAndStudentId(certificationId, student.getId())
                .orElseThrow(() -> notFound("Certification"));
        certificationRepository.delete(cert);
        return toResponse(student);
    }

    private void applyCertification(StudentCertification cert, CertificationRequest request) {
        cert.setName(request.certificationName().trim());
        cert.setOrganization(request.issuingOrganization().trim());
        cert.setIssueDate(request.issueDate());
        cert.setCredentialLink(blankToNull(request.credentialLink()));
    }

    // ===== Internships =====

    @Override
    @Transactional
    public ProfileResponse addInternship(User user, InternshipRequest request) {
        Student student = requireProfile(user);
        StudentInternship internship = new StudentInternship();
        internship.setStudent(student);
        applyInternship(internship, request);
        internshipRepository.save(internship);
        return toResponse(student);
    }

    @Override
    @Transactional
    public ProfileResponse updateInternship(User user, Long internshipId, InternshipRequest request) {
        Student student = requireProfile(user);
        StudentInternship internship = internshipRepository.findByIdAndStudentId(internshipId, student.getId())
                .orElseThrow(() -> notFound("Internship"));
        applyInternship(internship, request);
        internshipRepository.save(internship);
        return toResponse(student);
    }

    @Override
    @Transactional
    public ProfileResponse deleteInternship(User user, Long internshipId) {
        Student student = requireProfile(user);
        StudentInternship internship = internshipRepository.findByIdAndStudentId(internshipId, student.getId())
                .orElseThrow(() -> notFound("Internship"));
        internshipRepository.delete(internship);
        return toResponse(student);
    }

    private void applyInternship(StudentInternship internship, InternshipRequest request) {
        internship.setCompany(request.companyName().trim());
        internship.setRole(request.role().trim());
        internship.setDuration(request.duration().trim());
        internship.setDescription(blankToNull(request.description()));
    }

    // ===== Resume =====

    @Override
    @Transactional
    public ProfileResponse uploadResume(User user, MultipartFile file) {
        Student student = requireProfile(user);
        String storedPath = resumeStorage.store(student.getId(), file);
        // Replace any previous resume file on the server
        resumeStorage.delete(student.getResumePath());
        student.setResumePath(storedPath);
        String original = file.getOriginalFilename();
        student.setResumeFileName(original == null ? "resume"
                : (original.length() > 255 ? original.substring(original.length() - 255) : original));
        student.setResumeUpdatedAt(LocalDateTime.now());
        return toResponse(studentRepository.save(student));
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeDownload downloadResume(User user) {
        Student student = requireProfile(user);
        Resource resource = resumeStorage.loadAsResource(student.getResumePath());
        return new ResumeDownload(student.getResumeFileName(), contentTypeOf(student.getResumePath()), resource);
    }

    @Override
    @Transactional
    public ProfileResponse deleteResume(User user) {
        Student student = requireProfile(user);
        resumeStorage.delete(student.getResumePath());
        student.setResumePath(null);
        student.setResumeFileName(null);
        student.setResumeUpdatedAt(null);
        return toResponse(studentRepository.save(student));
    }

    private String contentTypeOf(String storedPath) {
        if (storedPath == null) {
            return "application/octet-stream";
        }
        String lower = storedPath.toLowerCase();
        if (lower.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (lower.endsWith(".doc")) {
            return "application/msword";
        }
        if (lower.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        return "application/octet-stream";
    }

    // ===== Helpers =====

    /** Resolve the caller's own placement profile, self-healing legacy accounts. */
    private Student requireProfile(User user) {
        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.PLACEMENT_OFFICER) {
            throw new UnauthorizedException("Administrative accounts do not have a student placement profile");
        }
        Student profile = studentRepository.findByUserId(user.getId()).orElse(null);
        if (profile == null) {
            // Legacy account without a linked profile - create it on first use
            Student created = new Student();
            created.setStudentName(user.getFullName());
            created.setEmail(user.getEmail());
            created.setDepartment(user.getDepartment());
            created.setYear(user.getYear());
            created.setPlacementStatus(Student.PlacementStatus.NOT_PLACED);
            created.setUserId(user.getId());
            return studentRepository.save(created);
        }
        return profile;
    }

    private ResourceNotFoundException notFound(String what) {
        return new ResourceNotFoundException(what + " not found");
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    /** Completeness checklist - weights sum to exactly 100. */
    private List<ProfileResponse.ChecklistItem> buildChecklist(Student s, List<StudentSkill> skills,
            List<StudentProject> projects, List<StudentCertification> certs,
            List<StudentInternship> internships) {
        List<ProfileResponse.ChecklistItem> items = new ArrayList<>();
        items.add(new ProfileResponse.ChecklistItem("Full name", notBlank(s.getStudentName()), 5));
        items.add(new ProfileResponse.ChecklistItem("Phone number", notBlank(s.getPhone()), 5));
        items.add(new ProfileResponse.ChecklistItem("Department", notBlank(s.getDepartment()), 5));
        items.add(new ProfileResponse.ChecklistItem("Year of study", s.getYear() != null, 5));
        items.add(new ProfileResponse.ChecklistItem("CGPA entered", s.getCgpa() != null, 10));
        items.add(new ProfileResponse.ChecklistItem("10th percentage", s.getTenthPercentage() != null, 5));
        items.add(new ProfileResponse.ChecklistItem("12th percentage", s.getTwelfthPercentage() != null, 5));
        items.add(new ProfileResponse.ChecklistItem("Graduation year", s.getGraduationYear() != null, 5));
        items.add(new ProfileResponse.ChecklistItem("Backlog information", s.getBacklogs() != null, 5));
        items.add(new ProfileResponse.ChecklistItem("Technical skills (at least 3)", skills.size() >= 3, 15));
        items.add(new ProfileResponse.ChecklistItem("Project (at least 1)", !projects.isEmpty(), 10));
        items.add(new ProfileResponse.ChecklistItem("Certification (at least 1)", !certs.isEmpty(), 10));
        items.add(new ProfileResponse.ChecklistItem("Internship (at least 1)", !internships.isEmpty(), 5));
        items.add(new ProfileResponse.ChecklistItem("Resume uploaded", s.getResumePath() != null, 10));
        return items;
    }

    private ProfileResponse toResponse(Student s) {
        List<StudentSkill> skills = skillRepository.findByStudentIdOrderByIdAsc(s.getId());
        List<StudentProject> projects = projectRepository.findByStudentIdOrderByIdAsc(s.getId());
        List<StudentCertification> certs = certificationRepository.findByStudentIdOrderByIdAsc(s.getId());
        List<StudentInternship> internships = internshipRepository.findByStudentIdOrderByIdAsc(s.getId());

        List<ProfileResponse.ChecklistItem> checklist = buildChecklist(s, skills, projects, certs, internships);
        int percent = (int) Math.round(checklist.stream()
                .filter(ProfileResponse.ChecklistItem::done)
                .mapToInt(ProfileResponse.ChecklistItem::weight)
                .sum());

        return new ProfileResponse(
                s.getId(),
                s.getUserId(),
                s.getStudentName(),
                s.getEmail(),
                s.getPhone(),
                s.getDepartment(),
                s.getYear(),
                s.getCgpa(),
                s.getGraduationYear(),
                s.getTenthPercentage(),
                s.getTwelfthPercentage(),
                s.getBacklogs(),
                s.getRegisterNumber(),
                s.getDegree(),
                s.getDateOfBirth() == null ? null : s.getDateOfBirth().format(DATE_FMT),
                s.getGender(),
                s.getLocation(),
                s.getAddress(),
                s.getCity(),
                s.getState(),
                s.getLinkedIn(),
                s.getGitHub(),
                s.getCodingProfiles(),
                s.getAchievements(),
                s.getSkills(),
                s.getPlacementStatus() == null ? null : s.getPlacementStatus().name(),
                new ProfileResponse.ResumeInfo(
                        s.getResumeFileName(),
                        s.getResumeUpdatedAt() == null ? null : s.getResumeUpdatedAt().format(DATE_TIME_FMT),
                        s.getResumePath() != null),
                skills.stream()
                        .map(k -> new ProfileResponse.SkillItem(k.getId(), k.getName(), k.getCategory().name()))
                        .toList(),
                projects.stream()
                        .map(p -> new ProfileResponse.ProjectItem(p.getId(), p.getName(), p.getDescription(),
                                p.getTechnologies(), p.getProjectLink(), p.getProjectRole()))
                        .toList(),
                certs.stream()
                        .map(c -> new ProfileResponse.CertificationItem(c.getId(), c.getName(), c.getOrganization(),
                                c.getIssueDate() == null ? null : c.getIssueDate().format(DATE_FMT), c.getCredentialLink()))
                        .toList(),
                internships.stream()
                        .map(i -> new ProfileResponse.InternshipItem(i.getId(), i.getCompany(), i.getRole(),
                                i.getDuration(), i.getDescription()))
                        .toList(),
                percent,
                checklist
        );
    }
}
