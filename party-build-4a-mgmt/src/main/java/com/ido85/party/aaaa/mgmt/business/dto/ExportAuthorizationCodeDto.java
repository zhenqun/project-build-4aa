package com.ido85.party.aaaa.mgmt.business.dto;

import java.io.Serializable;


public class ExportAuthorizationCodeDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7686532182579707947L;
	
	private String userId;
	
	private String idCard;
	
	private String relName;
	
	private String orgName;
	
	private String authorizationCode;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getRelName() {
		return relName;
	}

	public void setRelName(String relName) {
		this.relName = relName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}
}
