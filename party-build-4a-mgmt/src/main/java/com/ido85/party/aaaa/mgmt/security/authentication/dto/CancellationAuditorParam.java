package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

public class CancellationAuditorParam {
	
	@NotNull
	private List<String> auditorUserIds;

	public List<String> getAuditorUserIds() {
		return auditorUserIds;
	}

	public void setAuditorUserIds(List<String> auditorUserIds) {
		this.auditorUserIds = auditorUserIds;
	}

}
