package com.ido85.party.aaaa.mgmt.business.dto;

import java.util.List;

import lombok.Data;
@Data
public class GrantRoleParam {
	
	private String clientId;
	
	private List<RoleDto> roles;

}
