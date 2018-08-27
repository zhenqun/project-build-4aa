package com.ido85.party.sso.dto;

import java.util.List;
import java.util.Set;

public class ApplicationRoleDto {
	
	private Set<RoleDto> roles;
	
	private List<Long> applications;

	public Set<RoleDto> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleDto> roles) {
		this.roles = roles;
	}

	public List<Long> getApplications() {
		return applications;
	}

	public void setApplications(List<Long> applications) {
		this.applications = applications;
	}
}
