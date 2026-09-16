package com.placetrack.backend.service.impl;

import com.placetrack.backend.entity.Company;
import com.placetrack.backend.entity.Placement;
import com.placetrack.backend.entity.Student;
import com.placetrack.backend.exception.ResourceNotFoundException;
import com.placetrack.backend.repository.CompanyRepository;
import com.placetrack.backend.repository.PlacementRepository;
import com.placetrack.backend.repository.StudentRepository;
import com.placetrack.backend.service.PlacementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PlacementServiceImpl implements PlacementService {

    private final PlacementRepository placementRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;

    @Override
    public Placement createPlacement(Placement placement) {
        // Validate student exists
        Long studentId = placement.getStudent().getId();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        // Validate company exists
        Long companyId = placement.getCompany().getId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        placement.setStudent(student);
        placement.setCompany(company);

        Placement saved = placementRepository.save(placement);

        // Update student placement status if selected
        if (saved.getStatus() == Placement.PlacementStatus.SELECTED) {
            student.setPlacementStatus(Student.PlacementStatus.PLACED);
            studentRepository.save(student);
        }

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Placement> getAllPlacements() {
        return placementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Placement getPlacementById(Long id) {
        return placementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement", "id", id));
    }

    @Override
    public Placement updatePlacement(Long id, Placement updatedPlacement) {
        Placement existing = getPlacementById(id);

        // Validate and set student
        Long studentId = updatedPlacement.getStudent().getId();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        // Validate and set company
        Long companyId = updatedPlacement.getCompany().getId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        existing.setStudent(student);
        existing.setCompany(company);
        existing.setJobRole(updatedPlacement.getJobRole());
        existing.setPackageLpa(updatedPlacement.getPackageLpa());
        existing.setPlacementDate(updatedPlacement.getPlacementDate());
        existing.setStatus(updatedPlacement.getStatus());

        Placement saved = placementRepository.save(existing);

        // Update student placement status based on new status
        if (saved.getStatus() == Placement.PlacementStatus.SELECTED) {
            student.setPlacementStatus(Student.PlacementStatus.PLACED);
        } else {
            // Check if student has any other SELECTED placements
            boolean hasOtherSelected = placementRepository.findByStudentId(student.getId())
                    .stream()
                    .filter(p -> !p.getId().equals(id))
                    .anyMatch(p -> p.getStatus() == Placement.PlacementStatus.SELECTED);
            if (!hasOtherSelected) {
                student.setPlacementStatus(Student.PlacementStatus.NOT_PLACED);
            }
        }
        studentRepository.save(student);

        return saved;
    }

    @Override
    public void deletePlacement(Long id) {
        Placement placement = getPlacementById(id);
        Student student = placement.getStudent();

        placementRepository.delete(placement);

        // Recheck student status after deletion
        boolean hasSelectedPlacement = placementRepository.findByStudentId(student.getId())
                .stream()
                .anyMatch(p -> p.getStatus() == Placement.PlacementStatus.SELECTED);
        if (!hasSelectedPlacement) {
            student.setPlacementStatus(Student.PlacementStatus.NOT_PLACED);
            studentRepository.save(student);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Placement> filterPlacements(String status, Long companyId) {
        boolean hasStatus = StringUtils.hasText(status);
        boolean hasCompany = companyId != null;

        if (hasStatus && hasCompany) {
            Placement.PlacementStatus ps = Placement.PlacementStatus.valueOf(status.trim().toUpperCase());
            return placementRepository.findByStatusAndCompanyId(ps, companyId);
        } else if (hasStatus) {
            Placement.PlacementStatus ps = Placement.PlacementStatus.valueOf(status.trim().toUpperCase());
            return placementRepository.findByStatus(ps);
        } else if (hasCompany) {
            return placementRepository.findByCompanyId(companyId);
        } else {
            return placementRepository.findAll();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Placement> getRecentPlacements() {
        return placementRepository.findTop10ByOrderByPlacementDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalCompanies", companyRepository.count());
        stats.put("totalPlacements", placementRepository.count());
        stats.put("totalPlaced", studentRepository.countByPlacementStatus(Student.PlacementStatus.PLACED));
        stats.put("totalNotPlaced", studentRepository.countByPlacementStatus(Student.PlacementStatus.NOT_PLACED));
        stats.put("recentPlacements", placementRepository.findTop10ByOrderByPlacementDateDesc());
        return stats;
    }
}
