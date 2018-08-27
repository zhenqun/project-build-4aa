package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class AuditorDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3740642782785554517L;

	private String authorizationCode;
	
	private String auditorId;
	
	private String auditorName;
	
	private String idCard;
	
	private String manageName;
	
	private String state;
	
	private String telephone;
	
	private String createDate;
	
	private String manageId;

	private String isActivation;
}
