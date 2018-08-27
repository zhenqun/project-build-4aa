package com.ido85.party.aaaa.mgmt.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

public class ApplicationAllotParam {
	
	@NotNull
	private Long userId;
	
	private List<AllotApplicationDto> applications;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<AllotApplicationDto> getApplications() {
		return applications;
	}

	public void setApplications(List<AllotApplicationDto> applications) {
		this.applications = applications;
	}
}
