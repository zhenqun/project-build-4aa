package com.ido85.party.aaaa.mgmt.dto.role;

import java.io.Serializable;

import lombok.Data;
@Data
public class RolePermissionQueryDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String permissionDescription;
	
	private String permissionId;
	
	private String permissionName;

}
