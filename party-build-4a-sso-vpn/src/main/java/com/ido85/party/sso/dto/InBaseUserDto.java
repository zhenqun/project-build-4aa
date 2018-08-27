package com.ido85.party.sso.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class InBaseUserDto {
	
//	@NotNull
//	private String username;
	@NotNull
	private String telePhone;
	@NotNull
	private String name;
	@NotNull
	private String idCard;
	@NotNull
	private String password;
	@NotNull
	private String verifyCode;
	@NotNull
	private String authorizationCode;
	@NotNull
	private String vpnpwd;
}
