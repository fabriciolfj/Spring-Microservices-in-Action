package com.fabriciolfj.github.licensingservice.controller;

import com.fabriciolfj.github.licensingservice.model.License;
import com.fabriciolfj.github.licensingservice.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/organization/{organizationId}/license")
public class LicenseController {

    private final LicenseService licenseService;


    @GetMapping("/{licenseId}")
    public ResponseEntity<License> getLicense(@PathVariable("organizationId") final String organizationId, @PathVariable("licenseId") final String licenseId) {
        return ResponseEntity.ok(licenseService.getLicense(licenseId, organizationId));
    }

    @PutMapping
    public ResponseEntity<String> updateLicense(@PathVariable("organizationId") final String organizationId, @RequestBody final License license) {
        return ResponseEntity.ok(licenseService.updateLicense(license, organizationId));
    }

    @PostMapping
    public ResponseEntity<String> createLicense(@PathVariable("organizationId") final String organizationId,
                                                @RequestBody final License license,
                                                @RequestHeader(value = "Accept-Language", required = false) final Locale locale) {
        return ResponseEntity.ok(licenseService.createLicense(license, organizationId, locale));
    }

    @DeleteMapping("/{licenseId}")
    public ResponseEntity<String> deleteLicense(@PathVariable("organizationId") final String organizationId, @PathVariable("licenseId") final String licenseId) {
        return ResponseEntity.ok(licenseService.deleteLicense(licenseId, organizationId));
    }
}
