package com.ido85.party.aaaa.mgmt.business.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

public class ModifyBusinessUserStatusParam {
	@NotNull
	private List<String> businessUserIds;
	
	private String state;

	public List<String> getBusinessUserIds() {
		return businessUserIds;
	}

	public void setBusinessUserIds(List<String> businessUserIds) {
		this.businessUserIds = businessUserIds;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
