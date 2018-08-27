package com.ido85.party.aaa.authentication.dto;

import java.io.Serializable;

import lombok.Data;
@Data
public class RoleDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String roleId;
	
	private String roleName;
	
	private String userId;

}
