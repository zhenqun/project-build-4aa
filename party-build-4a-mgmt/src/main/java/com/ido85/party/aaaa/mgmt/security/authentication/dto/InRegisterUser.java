package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class InRegisterUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@NotNull
	private String email;
	
	private String identity = "0";
	@NotNull
	private String telePhone;//联系方式
	@NotNull
	private String password;
	@NotNull
	private String name ;//昵称
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public String getTelePhone() {
		return telePhone;
	}
	public void setTelePhone(String telePhone) {
		this.telePhone = telePhone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
	
	
	
	
	

}
