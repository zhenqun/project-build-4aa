package com.ido85.party.sso.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class AddAccountDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String orgId;
	
//	@NotNull
	private String username;
	
	@NotNull
	private String password;
	
	@NotNull
	private String idCard;

}
