package com.ido85.party.aaa.authentication.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class OldphoneVerifyParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2854714202767923411L;

	@NotNull
	private String oldPhone;
	
	@NotNull
	private String userId;
	
	@NotNull
	private String verifyCode;
	
}
