package com.ido85.party.sso.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class RoleDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String roleId;
	
	private String roleName;
	
	private String userId;
	
	private Long authencationInfoId;
	
	private String delFlag;
	
	private String uniqueKey;

	private String roleDes;

}
