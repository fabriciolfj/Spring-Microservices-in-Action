package com.fabriciolfj.github.licensingservice.mapper;

import com.fabriciolfj.github.licensingservice.events.model.OrganizationChangeModel;
import com.fabriciolfj.github.licensingservice.mapper.impl.OrganizationDecorated;
import com.fabriciolfj.github.licensingservice.model.Organization;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@DecoratedWith(OrganizationDecorated.class)
public interface OrganizationMapper {

    Organization toModel(final OrganizationChangeModel organizationChangeModel);
}
