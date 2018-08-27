package com.ido85.party.sso.dto;

import java.util.List;

import com.ido85.party.sso.security.authentication.domain.Permission;

import lombok.Data;

@Data
public class RoleDto {

	private String roleId;
	
	private String roleName;
	
	private String roleDescription;
	
	private String manageId;
	
	private String manageName;
	
	private String manageCode;
	
	private String clientId;
	
	private List<Permission> permissions;

}
