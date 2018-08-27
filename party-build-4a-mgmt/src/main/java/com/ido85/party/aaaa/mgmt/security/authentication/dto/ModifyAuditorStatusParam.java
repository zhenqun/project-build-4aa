package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

public class ModifyAuditorStatusParam {

	@NotNull
	private List<String> auditorUserIds;
	
	private String state;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<String> getAuditorUserIds() {
		return auditorUserIds;
	}

	public void setAuditorUserIds(List<String> auditorUserIds) {
		this.auditorUserIds = auditorUserIds;
	}
}
