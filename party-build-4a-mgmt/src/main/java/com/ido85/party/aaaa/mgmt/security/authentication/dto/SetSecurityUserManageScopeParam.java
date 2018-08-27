package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class SetSecurityUserManageScopeParam {
	
	private String securityUserId;
	@NotNull
	private String manageOrgId;
	@NotNull
	private String manageOrgName;
	@NotNull
	private String manageOrgCode;
	@NotNull
	private String clientId;
	@NotNull
	private String ouName;

}
