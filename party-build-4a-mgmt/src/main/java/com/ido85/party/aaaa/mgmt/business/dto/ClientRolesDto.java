package com.ido85.party.aaaa.mgmt.business.dto;

import lombok.Data;

import java.util.List;
@Data
public class ClientRolesDto {
	
	private String clientId;
	
	private String clientName;
	
	private Integer order;
	
	private List<RoleDto> roles;

	private String ifCanSearch;

	private String ifCanCreateRole;
}
