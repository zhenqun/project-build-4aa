package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

public class CancellationSecurityUserParam {

	@NotNull
	private List<String> securityUserIds;

	public List<String> getSecurityUserIds() {
		return securityUserIds;
	}

	public void setSecurityUserIds(List<String> securityUserIds) {
		this.securityUserIds = securityUserIds;
	}
}
