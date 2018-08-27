package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class InAddSecurityUserDto {
	
	@NotNull
	private String idCard;
	
	@NotNull
	private String relName;
	
	@NotNull
	private String telephone;

	@NotNull
	private String clientId;
	@NotEmpty
	private SetSecurityUserManageScopeParam scope;
	
}
