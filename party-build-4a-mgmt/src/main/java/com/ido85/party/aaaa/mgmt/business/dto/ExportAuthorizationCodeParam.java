package com.ido85.party.aaaa.mgmt.business.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

public class ExportAuthorizationCodeParam {
	@NotNull
	private List<String> ids;

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

}
