package com.fabriciolfj.github.licensingservice.repository;

import com.fabriciolfj.github.licensingservice.model.License;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, String> {

    List<License> findByOrganizationId(final String organizationId);
    Optional<License> findByOrganizationIdAndLicenseId(final String organizationId, final String licenseId);
}
