package com.fabriciolfj.github.licensingservice.service;

import com.fabriciolfj.github.licensingservice.model.License;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LicenseService {

    private final MessageSource messages;

    public License getLicense(final String licenseId, final String organizationId) {
        return License
                .builder()
                .id(new Random().nextInt(1000))
                .licenseId(licenseId)
                .organizationId(organizationId)
                .description("Software product")
                .productName("Ostock")
                .licenseType("full")
                .build();
    }

    public String createLicense(final License license, final String organizationId, final Locale locale) {
        if (!StringUtils.isEmpty(license)) {
            license.setOrganizationId(organizationId);
            return String.format(messages.getMessage("license.create.message", null, locale), license.toString());
        }

        return null;
    }

    public String updateLicense(final License license, final String organizationId) {
        if (!StringUtils.isEmpty(license)) {
            license.setOrganizationId(organizationId);
            return String.format(messages.getMessage("license.update.message", null, null), license.toString());
        }

        return null;
    }

    public String deleteLicense(final String licenseId, final String organizationId) {
        return String.format("Deleting license with id %s fo the organizaion %s", licenseId, organizationId);
    }
}
