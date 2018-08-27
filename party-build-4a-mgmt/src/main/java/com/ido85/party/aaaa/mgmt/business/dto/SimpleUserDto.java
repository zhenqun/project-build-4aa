package com.ido85.party.aaaa.mgmt.business.dto;

import java.io.Serializable;

public class SimpleUserDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7657791509377362286L;

	private String hash;
	
	private String orgId;
	
	private String orgName;
	
	private String sex;
	
	private String telephone;
	
	private String userId;

	private String orgCode;
	
	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
}
