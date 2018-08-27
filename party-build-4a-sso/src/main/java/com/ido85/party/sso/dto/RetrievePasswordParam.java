package com.ido85.party.sso.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class RetrievePasswordParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1526615890083862228L;
	@NotNull
	private String newPassword;
	@NotNull
	private String newVericode;
	@NotNull
	private String username;
	
}
