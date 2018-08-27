package com.ido85.party.aaaa.mgmt.business.dto;

import java.util.List;

public class SetManageScopeParam {
	
	private List<String> businessUserIds;
	
	private String manageOrgId;
	
	private String manageOrgName;
	
	private String manageOrgCode;

	public List<String> getBusinessUserIds() {
		return businessUserIds;
	}

	public void setBusinessUserIds(List<String> businessUserIds) {
		this.businessUserIds = businessUserIds;
	}

	public String getManageOrgId() {
		return manageOrgId;
	}

	public void setManageOrgId(String manageOrgId) {
		this.manageOrgId = manageOrgId;
	}

	public String getManageOrgName() {
		return manageOrgName;
	}

	public void setManageOrgName(String manageOrgName) {
		this.manageOrgName = manageOrgName;
	}

	public String getManageOrgCode() {
		return manageOrgCode;
	}

	public void setManageOrgCode(String manageOrgCode) {
		this.manageOrgCode = manageOrgCode;
	}

}
