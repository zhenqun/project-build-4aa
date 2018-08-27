package com.ido85.party.aaaa.mgmt.business.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

public class GrantBusinessUserParam {
	
	@NotNull
	private String businessUserId;
	
	private List<GrantRoleParam> clients;

	public List<GrantRoleParam> getClients() {
		return clients;
	}

	public void setClients(List<GrantRoleParam> clients) {
		this.clients = clients;
	}

	public String getBusinessUserId() {
		return businessUserId;
	}

	public void setBusinessUserId(String businessUserId) {
		this.businessUserId = businessUserId;
	}

}
