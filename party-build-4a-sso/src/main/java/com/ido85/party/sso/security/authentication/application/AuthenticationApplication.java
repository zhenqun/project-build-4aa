package com.ido85.party.sso.security.authentication.application;

import com.ido85.party.sso.dto.RoleDto;

import java.util.List;
import java.util.Map;

public interface AuthenticationApplication {

	List<RoleDto> authentiInfoQuery(List<String> uniqueKeys, String name,String idCard);

	/*
	 * nciic实名认证 
	 */
	Map<String, Object> nciicAuthentication(String idCard, String name);

	/**
	 * 修改认证信息状态为已使用
	 * @param authencationInfoIds
	 */
	void updateAuthencationState(List<Long> authencationInfoIds);
}
