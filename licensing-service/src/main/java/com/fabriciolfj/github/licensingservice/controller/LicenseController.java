package com.fabriciolfj.github.licensingservice.controller;

import com.fabriciolfj.github.licensingservice.model.License;
import com.fabriciolfj.github.licensingservice.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/organization/{organizationId}/license")
public class LicenseController {

    private final LicenseService licenseService;


    @GetMapping("/{licenseId}/{clientId}")
    public ResponseEntity<License> getLicense(@PathVariable("organizationId") final String organizationId,
                                              @PathVariable("licenseId") final String licenseId,
                                              @PathVariable("clientId") final String clientId) {
        var license = licenseService.getLicense(licenseId, organizationId, clientId);
        license.add(linkTo(methodOn(LicenseController.class).getLicense(organizationId, license.getLicenseId(), null)).withSelfRel(),
                linkTo(methodOn(LicenseController.class).createLicense(license)).withRel("createLicense"),
                linkTo(methodOn(LicenseController.class).updateLicense(license)).withRel("updateLicense"),
                linkTo(methodOn(LicenseController.class).deleteLicense(licenseId)).withRel("deleteLicense"));
        return ResponseEntity.ok().body(license);
    }

    @PutMapping
    public ResponseEntity<License> updateLicense(@RequestBody final License license) {
        return ResponseEntity.ok(licenseService.updateLicense(license));
    }

    @PostMapping
    public ResponseEntity<License> createLicense(@RequestBody final License license) {
        return ResponseEntity.ok(licenseService.createLicense(license));
    }

    @DeleteMapping("/{licenseId}")
    public ResponseEntity<String> deleteLicense(@PathVariable("licenseId") final String licenseId) {
        return ResponseEntity.ok(licenseService.deleteLicense(licenseId));
    }
}
