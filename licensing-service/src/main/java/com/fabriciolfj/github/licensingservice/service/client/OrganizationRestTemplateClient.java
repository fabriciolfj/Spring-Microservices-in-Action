package com.fabriciolfj.github.licensingservice.service.client;

import com.fabriciolfj.github.licensingservice.model.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrganizationRestTemplateClient {

    private final RestTemplate restTemplate;

    public Organization getOrganization(final String organizationId){
        var organization = request(organizationId);
        return organization;
    }

    private Organization request(final String organizationId) {
        final var restExchange =
                restTemplate.exchange(
                        "http://gateway:8072/organization/v1/organization/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}
