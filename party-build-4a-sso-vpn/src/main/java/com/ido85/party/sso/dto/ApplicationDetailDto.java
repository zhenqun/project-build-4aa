package com.ido85.party.sso.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ApplicationDetailDto implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String applicationId;
	
	private String applicationName;
	
	private String applicationUrl;
	
	private String applicationImage;
	
	private String applicationOrder;

}
