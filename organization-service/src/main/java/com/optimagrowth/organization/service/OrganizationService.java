package com.optimagrowth.organization.service;

import java.util.Optional;
import java.util.UUID;

import com.optimagrowth.organization.events.enuns.ActionEnuns;
import com.optimagrowth.organization.events.source.SimpleSourceBean;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimagrowth.organization.model.Organization;
import com.optimagrowth.organization.repository.OrganizationRepository;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository repository;
    private final SimpleSourceBean simpleSourceBean;

    public Organization findById(String organizationId) {
    	Optional<Organization> opt = repository.findById(organizationId);
        return (opt.isPresent()) ? opt.get() : null;
    }

    public Organization create(Organization organization){
    	organization.setId( UUID.randomUUID().toString());
        organization = repository.save(organization);

        simpleSourceBean.publishOrganizationChange(ActionEnuns.CREATE, organization.getId());
        return organization;

    }

    public void update(Organization organization){
    	repository.save(organization);
    }

    public void delete(Organization organization){
    	repository.deleteById(organization.getId());
    }
}