package com.ido85.party.aaaa.mgmt.business.dto;

import lombok.Data;

@Data
public class RoleDto {

	private Long roleId;
	
	private String roleName;
	
	private String manageId;
	
	private String manageName;
	
	private String manageCode;
	
	private String description;
	
	private String ouName;

	private String userManageName;

	private String detail;
	private Long relId;

	/**
	 * 管理范围的级别
	 */
	private String level;

	/**
	 * 管理范围的类别
	 */
	private String type;
	
}
