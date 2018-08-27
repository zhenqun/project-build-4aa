package com.ido85.party.aaaa.mgmt.dto;

import java.io.Serializable;

import lombok.Data;
@Data
public class OrganizationDto implements Serializable{

	/**
	 * 组织机构dto
	 */
	private static final long serialVersionUID = -991091652868484280L;

	private String orgId;
	
	private String orgName;
	
	private String code;
	
	private String parentId;
	
	private Integer orderId;
	
	private Integer hasChildren;
	
	private Integer level;
	
	private String ouName;

	private String type;

}
