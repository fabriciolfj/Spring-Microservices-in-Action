package com.fabriciolfj.github.licensingservice.events.model;

import lombok.*;

@Data
@Builder
public class OrganizationChangeModel {
	private String type;
	private String action;
	private String organizationId;
	private String correlationId;

	public OrganizationChangeModel(String type, String action, String organizationId, String correlationId) {
		super();
		this.type = type;
		this.action = action;
		this.organizationId = organizationId;
		this.correlationId = correlationId;
	}
}
