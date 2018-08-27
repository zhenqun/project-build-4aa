package com.ido85.party.aaaa.mgmt.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class BusinessUserRoleDto implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String roleId;
	
	private String roleName;
	
	private String description;
	
	private String manageId;
	
	private String manageName;

}
