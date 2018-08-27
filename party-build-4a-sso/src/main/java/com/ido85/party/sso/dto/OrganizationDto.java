package com.ido85.party.sso.dto;

import java.io.Serializable;

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

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(Integer hasChildren) {
		this.hasChildren = hasChildren;
	}
}
