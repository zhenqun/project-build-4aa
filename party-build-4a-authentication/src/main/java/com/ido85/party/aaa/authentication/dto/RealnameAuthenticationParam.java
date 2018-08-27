package com.ido85.party.aaa.authentication.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class RealnameAuthenticationParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String idCard;
	
	@NotNull
	private String name;

}
