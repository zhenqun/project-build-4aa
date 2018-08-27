package com.ido85.party.sso.dto.external;

import java.io.Serializable;

import lombok.Data;

@Data
public class EBranchUserInfoDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userId;
	
	private String logo;
	
	private String name;

}
