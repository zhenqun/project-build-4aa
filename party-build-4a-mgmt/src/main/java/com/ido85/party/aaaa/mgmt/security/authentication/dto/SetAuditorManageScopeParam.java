package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class SetAuditorManageScopeParam {
	
	@NotNull
	private String auditorUserId;
	@NotNull
	private String manageOrgId;
	@NotNull
	private String manageOrgName;
	@NotNull
	private String clientId;


}
