package com.ido85.party.sso.dto;

import javax.validation.constraints.NotNull;

public class RealnameAuthenticationParam {
	
	@NotNull
	private String username;
	@NotNull
	private String realName;
	@NotNull
	private String idCard;

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
