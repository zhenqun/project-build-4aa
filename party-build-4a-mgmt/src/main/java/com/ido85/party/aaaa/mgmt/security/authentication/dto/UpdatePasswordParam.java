package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdatePasswordParam implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String uid;
	
	@NotNull
	private String ou;
	
//	@NotNull
//	private String oldPwd;
	
	@NotNull
	private String newPwd;

}
