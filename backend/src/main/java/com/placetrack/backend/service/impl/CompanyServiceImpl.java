package com.placetrack.backend.service.impl;

import com.placetrack.backend.entity.Company;
import com.placetrack.backend.exception.ResourceInUseException;
import com.placetrack.backend.exception.ResourceNotFoundException;
import com.placetrack.backend.repository.CompanyRepository;
import com.placetrack.backend.repository.PlacementRepository;
import com.placetrack.backend.repository.StudentRepository;
import com.placetrack.backend.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final StudentRepository studentRepository;
    private final PlacementRepository placementRepository;

    @Override
    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }

    @Override
    public Company updateCompany(Long id, Company updatedCompany) {
        Company existingCompany = getCompanyById(id);

        // Core identity fields
        existingCompany.setName(updatedCompany.getName());
        existingCompany.setIndustry(updatedCompany.getIndustry());
        existingCompany.setWebsite(updatedCompany.getWebsite());
        existingCompany.setLocation(updatedCompany.getLocation());
        existingCompany.setDescription(updatedCompany.getDescription());

        // Contact info
        existingCompany.setContactPerson(updatedCompany.getContactPerson());
        existingCompany.setContactEmail(updatedCompany.getContactEmail());

        // Recruitment context
        existingCompany.setJobRole(updatedCompany.getJobRole());
        existingCompany.setPackageLpa(updatedCompany.getPackageLpa());
        existingCompany.setDriveEligibilityCgpa(updatedCompany.getDriveEligibilityCgpa());
        existingCompany.setDriveDate(updatedCompany.getDriveDate());
        existingCompany.setApplicationDeadline(updatedCompany.getApplicationDeadline());
        existingCompany.setDriveLocation(updatedCompany.getDriveLocation());
        existingCompany.setSelectionProcess(updatedCompany.getSelectionProcess());
        existingCompany.setDriveDescription(updatedCompany.getDriveDescription());
        existingCompany.setDriveStatus(updatedCompany.getDriveStatus());
        existingCompany.setDriveEligibilitySummary(updatedCompany.getDriveEligibilitySummary());
        existingCompany.setLogoUrl(updatedCompany.getLogoUrl());

        return companyRepository.save(existingCompany);
    }

    @Override
    public void deleteCompany(Long id) {
        Company company = getCompanyById(id);
        // Guard against deleting a company that still has placement records
        long placementCount = placementRepository.countByCompanyId(id);
        if (placementCount > 0) {
            throw new ResourceInUseException(
                    "Company cannot be deleted because " + placementCount +
                    " placement record(s) exist for this company. Delete the placements first.");
        }
        companyRepository.delete(company);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> searchCompanies(String query) {
        if (!StringUtils.hasText(query)) {
            return companyRepository.findAll();
        }
        return companyRepository.searchCompanies(query.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCompanyStats() {
        LocalDate today = LocalDate.now();

        Double avg = studentRepository.findAverageCgpa();
        double avgCgpa = (avg == null) ? 0.0 : Math.round(avg * 10.0) / 10.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCompanies", companyRepository.count());
        // Active drives = scheduled today or in the future
        stats.put("activeDrives", companyRepository.countByDriveDateGreaterThanEqual(today));
        // Upcoming drives = scheduled within the next 30 days
        stats.put("upcomingDrives", companyRepository.countByDriveDateBetween(today, today.plusDays(30)));
        // Eligible opportunities = open drives whose min CGPA the average student meets
        stats.put("eligibleOpportunities", companyRepository.countOpenEligible(today, avgCgpa));
        stats.put("averageStudentCgpa", avgCgpa);
        return stats;
    }
}
