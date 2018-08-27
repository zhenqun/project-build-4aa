package com.ido85.party.sso.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class PermissionDto implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String permissionId;
	
	private String permissionName;
	
	private String permissionDescription;
	
	private String clientId;

}
