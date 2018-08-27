package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
@Data
public class ModifySecurityUserStatusParam {
	@NotNull
	private List<String> securityUserIds;
	
	private String state;

}
