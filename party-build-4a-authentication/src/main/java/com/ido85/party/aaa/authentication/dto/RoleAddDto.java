package com.ido85.party.aaa.authentication.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class RoleAddDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String clientId;
	
	private String roleName;
	
	private String description;
}
