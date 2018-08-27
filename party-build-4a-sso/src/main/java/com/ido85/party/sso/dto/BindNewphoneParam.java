package com.ido85.party.sso.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class BindNewphoneParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3394534604312226706L;

	@NotNull
	private String newPhone;
	
	@NotNull
	private String userId;
	
	@NotNull
	private String verifyCode;
	
}
