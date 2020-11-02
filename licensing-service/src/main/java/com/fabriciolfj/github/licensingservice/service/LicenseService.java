package com.fabriciolfj.github.licensingservice.service;

import com.fabriciolfj.github.licensingservice.config.ServiceConfig;
import com.fabriciolfj.github.licensingservice.controller.exceptions.LicenseNotfound;
import com.fabriciolfj.github.licensingservice.controller.exceptions.OrganizationNotfound;
import com.fabriciolfj.github.licensingservice.model.License;
import com.fabriciolfj.github.licensingservice.model.Organization;
import com.fabriciolfj.github.licensingservice.repository.LicenseRepository;
import com.fabriciolfj.github.licensingservice.service.client.OrganizationDiscoveryClient;
import com.fabriciolfj.github.licensingservice.service.client.OrganizationFeignClient;
import com.fabriciolfj.github.licensingservice.service.client.OrganizationRestTemplateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LicenseService {

    private final MessageSource messages;
    private final LicenseRepository licenseRepository;
    private final ServiceConfig serviceConfig;
    private final OrganizationDiscoveryClient organizationDiscoveryClient;
    private final OrganizationFeignClient organizationFeignClient;
    private final OrganizationRestTemplateClient organizationRestTemplateClient;

    public License getLicense(final String licenseId, final String organizationId, final String clientType){
        var license = getLicense(licenseId, organizationId);
        var organization = getOrganization(organizationId, clientType);
        log.info("Organization : {}", organization);

        license.setOrganizationName(organization.getName());
        license.setContactName(organization.getContactName());
        license.setContactEmail(organization.getContactEmail());
        license.setContactPhone(organization.getContactPhone());

        return license.withComment(serviceConfig.getProperty());
    }

    public License getLicense(final String licenseId, final String organizationId) {
        return licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId)
                .map(l -> l.withComment(serviceConfig.getProperty()))
                .orElseThrow(() ->
                        new LicenseNotfound(String.format(messages.getMessage("license.search.error.message", null, null),licenseId, organizationId)));
    }

    public License createLicense(final License license) {
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);

        return license.withComment(serviceConfig.getProperty());
    }

    public License updateLicense(final License license) {
        licenseRepository.save(getLicense(license.getLicenseId(), license.getOrganizationId()));
        return license.withComment(serviceConfig.getProperty());
    }

    public String deleteLicense(final String licenseId) {
        try {
            licenseRepository.deleteById(licenseId);
        } catch (Exception e) {}
        return String.format("Deleting license with id %s", licenseId);
    }

    private Organization getOrganization(final String organizationId, final String clientType) {
        try {
            return retrieveOrganizationInfo(organizationId, clientType);
        } catch (Exception e) {
            throw new OrganizationNotfound(e.getMessage());
        }
    }

    private Organization retrieveOrganizationInfo(String organizationId, String clientType) {
        switch (clientType) {
            case "feign":
                System.out.println("I am using the feign client");
                return organizationFeignClient.getOrganization(organizationId);
            case "rest":
                System.out.println("I am using the rest client");
                return organizationRestTemplateClient.getOrganization(organizationId);
            case "discovery":
                System.out.println("I am using the discovery client");
                return organizationDiscoveryClient.getOrganization(organizationId);
            default:
                return organizationRestTemplateClient.getOrganization(organizationId);
        }
    }
}
