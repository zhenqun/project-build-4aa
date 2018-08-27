package com.ido85.party.aaaa.mgmt.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class BusinessUserRoleQueryParam implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String userId;
	@NotNull
	private String clientId;

}
