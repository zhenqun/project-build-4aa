package com.ido85.party.aaa.authentication.application;

import java.util.List;
import java.util.Map;

import com.ido85.party.aaa.authentication.dto.RoleDto;

public interface Authentication {

	List<RoleDto> authentiInfoQuery(List<String> uniqueKey,String relName);

	/*
	 * nciic实名认证 
	 */
	Map<String, Object> nciicAuthentication(String idCard, String name);

}
