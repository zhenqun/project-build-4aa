package com.ido85.party.aaa.authentication.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class UserPwdModifyParam {
	
	@NotNull
//	@NotBlank
	private String userId;
	
	@NotNull
//	@NotBlank
	@Length(max=20,min=6)
	private String newPassword;
	
	@NotNull
//	@NotBlank
	private String oldPassword;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
