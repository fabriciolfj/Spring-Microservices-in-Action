package com.fabriciolfj.github.licensingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("organization")
public class Organization extends RepresentationModel<Organization> {

    @Id
	private String id;
    private String name;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    
}
