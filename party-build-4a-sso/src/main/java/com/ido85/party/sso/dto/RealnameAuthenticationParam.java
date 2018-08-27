package com.ido85.party.sso.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class RealnameAuthenticationParam {
	
	@NotNull
	private String name;
	@NotNull
	private String idCard;

}
