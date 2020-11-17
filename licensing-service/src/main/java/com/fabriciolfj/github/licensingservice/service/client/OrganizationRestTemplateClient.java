package com.fabriciolfj.github.licensingservice.service.client;

import com.fabriciolfj.github.licensingservice.model.Organization;
import com.fabriciolfj.github.licensingservice.repository.OrganizationRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrganizationRestTemplateClient {

    private final RestTemplate restTemplate;
    private final OrganizationRedisRepository redisRepository;

    public Organization getOrganization(final String organizationId){
        return redisRepository.findById(organizationId)
                .orElseGet(() -> {
                    log.info("Informação não localizada no redis, realizando requisição.");
                    var organization = request(organizationId);
                    redisRepository.save(organization);
                    return organization;
                });
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
