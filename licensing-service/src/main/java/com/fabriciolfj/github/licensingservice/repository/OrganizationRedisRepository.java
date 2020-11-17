package com.fabriciolfj.github.licensingservice.repository;

import com.fabriciolfj.github.licensingservice.model.Organization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRedisRepository extends CrudRepository<Organization, String> {
}
