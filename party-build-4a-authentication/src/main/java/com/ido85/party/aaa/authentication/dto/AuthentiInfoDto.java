package com.ido85.party.aaa.authentication.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AuthentiInfoDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String uniqueKey;
	
	@NotNull
	private String relName;
	
	@NotNull
	private String clientId;
	
	private String clientName;
	
	@NotNull
	private Long roleId;
	
	@NotNull
	private String userId;

	@NotNull
	private String idCard;

}
