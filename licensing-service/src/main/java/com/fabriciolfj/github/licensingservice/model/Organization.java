package com.fabriciolfj.github.licensingservice.model;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class Organization extends RepresentationModel<Organization> {

	private String id;
    private String name;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    
}
