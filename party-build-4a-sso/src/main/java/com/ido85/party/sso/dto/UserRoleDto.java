package com.ido85.party.sso.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserRoleDto implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String roleName;
	
	private String userId;

}
