package com.ido85.party.aaaa.mgmt.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class AddAccountDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String ou;
	
//	@NotNull
	private String password;
	
	@NotNull
	private String idCard;

}
