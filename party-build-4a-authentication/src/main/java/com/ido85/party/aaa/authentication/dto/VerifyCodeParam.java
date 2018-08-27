package com.ido85.party.aaa.authentication.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class VerifyCodeParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4709672883176317781L;
	@NotNull
	private String telephone;
	@NotNull
	private String type;
	@NotNull
	private String veifyCode;
}
