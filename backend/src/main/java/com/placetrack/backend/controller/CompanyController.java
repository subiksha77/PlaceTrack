package com.placetrack.backend.controller;

import com.placetrack.backend.entity.Company;
import com.placetrack.backend.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    // POST /api/companies - Create a new company
    @PostMapping
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        Company created = companyService.createCompany(company);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // GET /api/companies - Get all companies (with optional search)
    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies(
            @RequestParam(required = false) String search) {

        List<Company> companies;
        if (search != null && !search.isBlank()) {
            companies = companyService.searchCompanies(search);
        } else {
            companies = companyService.getAllCompanies();
        }

        return ResponseEntity.ok(companies);
    }

    // GET /api/companies/stats - Company dashboard statistics (computed from DB)
    // NOTE: declared before /{id} so "stats" is treated as a literal path
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCompanyStats() {
        return ResponseEntity.ok(companyService.getCompanyStats());
    }

    // GET /api/companies/{id} - Get company by ID
    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    // PUT /api/companies/{id} - Update company
    @PutMapping("/{id}")
    public ResponseEntity<Company> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody Company company) {
        Company updated = companyService.updateCompany(id, company);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/companies/{id} - Delete company
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(Map.of("message", "Company deleted successfully"));
    }
}
