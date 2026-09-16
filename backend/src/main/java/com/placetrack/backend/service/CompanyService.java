package com.placetrack.backend.service;

import com.placetrack.backend.entity.Company;

import java.util.List;
import java.util.Map;

public interface CompanyService {

    Company createCompany(Company company);

    List<Company> getAllCompanies();

    Company getCompanyById(Long id);

    Company updateCompany(Long id, Company company);

    void deleteCompany(Long id);

    List<Company> searchCompanies(String query);

    Map<String, Object> getCompanyStats();
}
