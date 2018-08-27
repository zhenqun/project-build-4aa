package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class InLoginUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@NotNull
	private String userName;//邮箱
	
	@NotNull
	private String password;
	
	private String isRemember;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIsRemember() {
		return isRemember;
	}

	public void setIsRemember(String isRemember) {
		this.isRemember = isRemember;
	}
	
	
	
	
	
	
	
	

}
