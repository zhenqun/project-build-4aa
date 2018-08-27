package com.ido85.party.aaaa.mgmt.internet.dto;

import java.util.List;

public class IntegerUserQueryParam {
	
	private String username;
	
	private String telephone;
	
	private String idCard;
	
	private String orgId;
	
	private String status;
	
	private String identity;
	
	private List<Long> applicationIds;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public List<Long> getApplicationIds() {
		return applicationIds;
	}

	public void setApplicationIds(List<Long> applicationIds) {
		this.applicationIds = applicationIds;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	private Integer pageNo;
	
	private Integer pageSize;
}
