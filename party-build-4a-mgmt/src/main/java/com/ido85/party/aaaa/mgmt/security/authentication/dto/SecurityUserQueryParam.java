package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class SecurityUserQueryParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7137233992960702704L;

	private String securityName;
	
	private String manageOrgId;
	
	private String state;
	
	private Integer pageNo;
	
	private Integer pageSize;
	
	private String clientId;

	private String isActivation;
}
