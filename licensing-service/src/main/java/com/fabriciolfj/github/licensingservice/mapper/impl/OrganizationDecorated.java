package com.fabriciolfj.github.licensingservice.mapper.impl;

import com.fabriciolfj.github.licensingservice.events.model.OrganizationChangeModel;
import com.fabriciolfj.github.licensingservice.mapper.OrganizationMapper;
import com.fabriciolfj.github.licensingservice.model.Organization;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class OrganizationDecorated implements OrganizationMapper {

    @Override
    public Organization toModel(final OrganizationChangeModel model) {
        return Organization
                .builder()
                .contactEmail("")
                .contactName("")
                .contactPhone("")
                .id(model.getOrganizationId())
                .name("")
                .build();
    }
}
