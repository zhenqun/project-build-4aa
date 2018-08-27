package com.ido85.party.sso.dto;

import javax.validation.constraints.NotNull;

public class UserPwdModifyParam {
	
	@NotNull
	private String username;
	
	@NotNull
	private String newPassword;
	
	@NotNull
	private String oldPassword;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
}
