package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import javax.validation.constraints.NotNull;

public class InAddAuditorDto {
	
	@NotNull
	private String idCard;
	
	@NotNull
	private String relName;

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getRelName() {
		return relName;
	}

	public void setRelName(String relName) {
		this.relName = relName;
	}
}
