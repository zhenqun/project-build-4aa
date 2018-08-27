package com.ido85.party.aaaa.mgmt.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class CheckRoleParam implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String roleManageId;
	
	private String clientId;
	
	private String type;//0：业务管理员   1：安全员  2：审计员
	
}
