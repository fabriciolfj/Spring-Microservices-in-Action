package com.fabriciolfj.github.licensingservice.service;

import com.fabriciolfj.github.licensingservice.config.ServiceConfig;
import com.fabriciolfj.github.licensingservice.model.License;
import com.fabriciolfj.github.licensingservice.repository.LicenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LicenseService {

    private final MessageSource messages;
    private final LicenseRepository licenseRepository;
    private final ServiceConfig serviceConfig;

    public License getLicense(final String licenseId, final String organizationId) {
        return licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId)
                .map(l -> l.withComment(serviceConfig.getProperty()))
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format(messages.getMessage("license.search.error.message", null, null),licenseId, organizationId)));
    }

    public License createLicense(final License license) {
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);

        return license.withComment(serviceConfig.getProperty());
    }

    public License updateLicense(final License license) {
        licenseRepository.save(license);
        return license.withComment(serviceConfig.getProperty());
    }

    public String deleteLicense(final String licenseId) {
        try {
            licenseRepository.deleteById(licenseId);
        } catch (Exception e) {}
        return String.format("Deleting license with id %s", licenseId);
    }
}
