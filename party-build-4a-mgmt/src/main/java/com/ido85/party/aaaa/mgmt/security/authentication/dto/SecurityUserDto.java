package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class SecurityUserDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2446176039850804353L;

	private String authorizationCode;
	
	private String securityUserId;
	
	private String securityUserName;
	
	private String idCard;
	
	private String state;
	
	private String telephone;
	
	private String createDate;
	
	private String manageId;
	
	private String manageName;

	private String isActivation;
	
}
