package com.ido85.party.sso.dto;

import java.util.List;

public class ClientRolesDto {
	
	private String clientId;
	
	private String clientName;
	
	private List<RoleDto> roles;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public List<RoleDto> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleDto> roles) {
		this.roles = roles;
	}
}
