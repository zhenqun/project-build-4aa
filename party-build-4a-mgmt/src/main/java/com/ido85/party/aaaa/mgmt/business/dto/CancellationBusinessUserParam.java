package com.ido85.party.aaaa.mgmt.business.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

public class CancellationBusinessUserParam {
	@NotNull
	private List<String> businessUserIds;

	public List<String> getBusinessUserIds() {
		return businessUserIds;
	}

	public void setBusinessUserIds(List<String> businessUserIds) {
		this.businessUserIds = businessUserIds;
	}
}
