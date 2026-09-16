package com.placetrack.backend.service.impl;

import com.placetrack.backend.dto.EligibilityResultDto;
import com.placetrack.backend.dto.JobRecommendationDto;
import com.placetrack.backend.entity.*;
import com.placetrack.backend.exception.ResourceNotFoundException;
import com.placetrack.backend.repository.*;
import com.placetrack.backend.security.CurrentUserResolver;
import com.placetrack.backend.service.EligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EligibilityServiceImpl implements EligibilityService {

    private final StudentRepository studentRepository;
    private final JobOpportunityRepository jobRepository;
    private final StudentSkillRepository skillRepository;
    private final CurrentUserResolver currentUser;

    @Override
    @Transactional(readOnly = true)
    public EligibilityResultDto checkEligibilityForUser(String authToken, Long jobId) {
        User user = currentUser.require(authToken);
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user: " + user.getEmail()));
        return checkEligibility(student.getId(), jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public EligibilityResultDto checkEligibility(Long studentId, Long jobId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        JobOpportunity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobOpportunity", "id", jobId));

        List<EligibilityResultDto.Criterion> criteria = new ArrayList<>();
        boolean overallEligible = true;

        // 1. CGPA Criterion
        if (job.getMinCgpa() != null && job.getMinCgpa() > 0) {
            double actualCgpa = student.getCgpa() != null ? student.getCgpa() : 0.0;
            boolean passed = actualCgpa >= job.getMinCgpa();
            if (!passed) overallEligible = false;
            criteria.add(new EligibilityResultDto.Criterion(
                    "CGPA Requirement",
                    passed,
                    "Min " + job.getMinCgpa() + " CGPA",
                    actualCgpa > 0 ? actualCgpa + " CGPA" : "Not Provided",
                    passed ? "CGPA requirement satisfied" : "Student CGPA (" + actualCgpa + ") is below required " + job.getMinCgpa()
            ));
        }

        // 2. Department Criterion
        if (job.getEligibleDepartments() != null && !job.getEligibleDepartments().isBlank()) {
            String studentDept = student.getDepartment() != null ? student.getDepartment().trim().toUpperCase() : "";
            List<String> allowedDepts = Arrays.stream(job.getEligibleDepartments().split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .toList();
            boolean passed = !studentDept.isEmpty() && (allowedDepts.contains(studentDept) || allowedDepts.contains("ALL"));
            if (!passed) overallEligible = false;
            criteria.add(new EligibilityResultDto.Criterion(
                    "Department Eligibility",
                    passed,
                    job.getEligibleDepartments(),
                    studentDept.isEmpty() ? "Not Specified" : studentDept,
                    passed ? "Department is eligible" : "Department (" + studentDept + ") is not in eligible list (" + job.getEligibleDepartments() + ")"
            ));
        }

        // 3. Academic Year / Graduation Year Criterion
        if (job.getGraduationYear() != null && job.getGraduationYear() > 0) {
            Integer gradYear = student.getGraduationYear();
            boolean passed = gradYear != null && gradYear.equals(job.getGraduationYear());
            if (!passed) overallEligible = false;
            criteria.add(new EligibilityResultDto.Criterion(
                    "Graduation Batch",
                    passed,
                    job.getGraduationYear() + " Batch",
                    gradYear != null ? gradYear + " Batch" : "Not Specified",
                    passed ? "Graduation year eligible" : "Required batch is " + job.getGraduationYear() + ", student batch is " + (gradYear != null ? gradYear : "unspecified")
            ));
        }

        // 4. Backlogs Criterion
        if (job.getBacklogsAllowed() != null) {
            int actualBacklogs = student.getBacklogs() != null ? student.getBacklogs() : 0;
            boolean passed = actualBacklogs <= job.getBacklogsAllowed();
            if (!passed) overallEligible = false;
            criteria.add(new EligibilityResultDto.Criterion(
                    "Backlog Allowance",
                    passed,
                    "Max " + job.getBacklogsAllowed() + " backlog(s)",
                    actualBacklogs + " backlog(s)",
                    passed ? "Backlog criteria satisfied" : "Student has " + actualBacklogs + " backlogs, maximum allowed is " + job.getBacklogsAllowed()
            ));
        }

        // 5. Skills Matching
        Set<String> studentSkillSet = getStudentSkillSet(student);
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        if (job.getRequiredSkills() != null && !job.getRequiredSkills().isBlank()) {
            String[] reqSkills = job.getRequiredSkills().split(",");
            for (String req : reqSkills) {
                String clean = req.trim();
                if (clean.isEmpty()) continue;
                boolean found = studentSkillSet.stream().anyMatch(s -> s.equalsIgnoreCase(clean));
                if (found) {
                    matchedSkills.add(clean);
                } else {
                    missingSkills.add(clean);
                }
            }

            boolean passed = missingSkills.isEmpty();
            // Note: Skill gap is flagged, but we present clear breakdown
            criteria.add(new EligibilityResultDto.Criterion(
                    "Required Skills",
                    passed,
                    job.getRequiredSkills(),
                    matchedSkills.isEmpty() ? "None matched" : String.join(", ", matchedSkills),
                    passed ? "All required skills matched" : "Missing required skill(s): " + String.join(", ", missingSkills)
            ));
        }

        String summary = overallEligible
                ? "You meet all academic and qualification criteria for this role."
                : "You do not currently satisfy all the criteria for this opportunity.";

        return new EligibilityResultDto(
                job.getId(),
                job.getJobRole(),
                job.getCompany() != null ? job.getCompany().getName() : "Company",
                overallEligible,
                summary,
                criteria,
                matchedSkills,
                missingSkills
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobRecommendationDto> getRecommendationsForUser(String authToken) {
        User user = currentUser.require(authToken);
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user: " + user.getEmail()));
        return getRecommendations(student.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobRecommendationDto> getRecommendations(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        Set<String> studentSkills = getStudentSkillSet(student);
        List<JobOpportunity> openJobs = jobRepository.findByStatus(JobOpportunity.OpportunityStatus.OPEN);
        if (openJobs.isEmpty()) {
            openJobs = jobRepository.findAll();
        }

        List<JobRecommendationDto> recommendations = new ArrayList<>();

        for (JobOpportunity job : openJobs) {
            int score = 0;
            List<String> matched = new ArrayList<>();
            List<String> missing = new ArrayList<>();

            // 1. Skill overlap (up to 50 pts)
            if (job.getRequiredSkills() != null && !job.getRequiredSkills().isBlank()) {
                String[] reqs = job.getRequiredSkills().split(",");
                int totalReqs = 0;
                int matchedCount = 0;
                for (String r : reqs) {
                    String clean = r.trim();
                    if (clean.isEmpty()) continue;
                    totalReqs++;
                    if (studentSkills.stream().anyMatch(s -> s.equalsIgnoreCase(clean))) {
                        matched.add(clean);
                        matchedCount++;
                    } else {
                        missing.add(clean);
                    }
                }
                if (totalReqs > 0) {
                    score += (int) Math.round((double) matchedCount / totalReqs * 50);
                }
            } else {
                score += 30; // default skill score if job lists no specific skills
            }

            // 2. CGPA fit (up to 20 pts)
            boolean cgpaOk = true;
            if (job.getMinCgpa() != null && job.getMinCgpa() > 0) {
                if (student.getCgpa() != null && student.getCgpa() >= job.getMinCgpa()) {
                    score += 20;
                } else {
                    cgpaOk = false;
                }
            } else {
                score += 20;
            }

            // 3. Department match (up to 15 pts)
            boolean deptOk = true;
            if (job.getEligibleDepartments() != null && !job.getEligibleDepartments().isBlank()) {
                String dept = student.getDepartment() != null ? student.getDepartment().trim().toUpperCase() : "";
                if (!dept.isEmpty() && job.getEligibleDepartments().toUpperCase().contains(dept)) {
                    score += 15;
                } else {
                    deptOk = false;
                }
            } else {
                score += 15;
            }

            // 4. Backlog fit (up to 15 pts)
            boolean backlogsOk = true;
            if (job.getBacklogsAllowed() != null) {
                int bl = student.getBacklogs() != null ? student.getBacklogs() : 0;
                if (bl <= job.getBacklogsAllowed()) {
                    score += 15;
                } else {
                    backlogsOk = false;
                }
            } else {
                score += 15;
            }

            boolean isEligible = cgpaOk && deptOk && backlogsOk;

            // Generate transparent reason
            String reason;
            if (!matched.isEmpty() && isEligible) {
                reason = "Matched skills (" + String.join(", ", matched) + ") and fully satisfies academic criteria.";
            } else if (!matched.isEmpty()) {
                reason = "Strong skill match (" + String.join(", ", matched) + "), but review academic criteria.";
            } else if (isEligible) {
                reason = "Academically eligible for " + (job.getCompany() != null ? job.getCompany().getName() : "this role") + ". Consider adding " + (missing.isEmpty() ? "skills" : String.join(", ", missing)) + ".";
            } else {
                reason = "Job available for your department; requires additional skill prerequisites.";
            }

            recommendations.add(new JobRecommendationDto(
                    job.getId(),
                    job.getJobRole(),
                    job.getCompany() != null ? job.getCompany().getId() : null,
                    job.getCompany() != null ? job.getCompany().getName() : "Company",
                    job.getPackageLpa(),
                    job.getWorkLocation(),
                    Math.min(100, Math.max(10, score)),
                    isEligible,
                    matched,
                    missing,
                    reason
            ));
        }

        // Sort by match percentage descending
        recommendations.sort(Comparator.comparingInt(JobRecommendationDto::matchPercentage).reversed());
        return recommendations;
    }

    private Set<String> getStudentSkillSet(Student student) {
        Set<String> set = new HashSet<>();
        // From StudentSkill entities
        List<StudentSkill> skills = skillRepository.findByStudentIdOrderByIdAsc(student.getId());
        for (StudentSkill s : skills) {
            if (s.getName() != null) set.add(s.getName().trim().toLowerCase());
        }
        // From free-text skills column
        if (student.getSkills() != null) {
            for (String s : student.getSkills().split("[,;|]")) {
                String clean = s.trim().toLowerCase();
                if (!clean.isEmpty()) set.add(clean);
            }
        }
        return set;
    }
}
