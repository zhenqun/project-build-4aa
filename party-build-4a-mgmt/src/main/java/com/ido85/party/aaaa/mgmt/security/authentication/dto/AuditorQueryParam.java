package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class AuditorQueryParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -327373290377613048L;

	private String auditorName;
	
	private String manageOrgId;
	
	private String state;
	
	private Integer pageNo;
	
	private Integer pageSize;
	
	private String clientId;

	private String isActivation;


}
