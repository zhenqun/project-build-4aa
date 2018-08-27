package com.ido85.party.aaaa.mgmt.dto.ldap;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class AddBatchAccountDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@NotNull
	private String username;

}
