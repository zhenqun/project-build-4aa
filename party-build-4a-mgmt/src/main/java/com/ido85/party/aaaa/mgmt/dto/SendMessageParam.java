package com.ido85.party.aaaa.mgmt.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class SendMessageParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String telephone;
	
	/**
	 * 2000：注册； 2001：找回密码 ； 2002：验证原手机号； 2003：验证新手机号
	 */
	@NotNull
	private String type;
	

}
