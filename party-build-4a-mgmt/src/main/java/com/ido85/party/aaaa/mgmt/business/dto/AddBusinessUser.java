package com.ido85.party.aaaa.mgmt.business.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class AddBusinessUser {
	

	private String idCard;
	

	private String relName;
	

	private String telephone;

	@NotNull
	private String clientId;
	
	private List<RoleDto> roles;
	
}
