package com.ido85.party.sso.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class InBaseUserDto {
//	@NotNull
//	private String username;
	
	@NotNull
	private String verifyCode;
	
	@NotNull
	private String telePhone;
	
	@NotNull
	private String name;
	
	@NotNull
	private String password;
	
	@NotNull
	private String idCard;
	
	private List<String> uniqueKey;

}
