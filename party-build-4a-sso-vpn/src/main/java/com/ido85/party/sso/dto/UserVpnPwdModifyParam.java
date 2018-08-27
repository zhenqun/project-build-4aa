package com.ido85.party.sso.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserVpnPwdModifyParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String password;
	
	private String vpn;

}
