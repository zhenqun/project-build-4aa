package com.ido85.party.aaaa.mgmt.business.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class BusinessUserQueryParam {
	
	private String businessUserName;
	
	private String manageOrgId;
	
	private String state;
	@NotNull
	private String clientId;

	private Integer pageNo;
	
	private Integer pageSize;
	
	private String level;

	private String isActivation;

}
