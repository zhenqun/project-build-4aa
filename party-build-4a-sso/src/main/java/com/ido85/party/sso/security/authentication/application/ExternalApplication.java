package com.ido85.party.sso.security.authentication.application;

import java.util.List;

import com.ido85.party.sso.security.authentication.domain.User;

public interface ExternalApplication {
	/***
	 * 根据用户id获取用户信息
	 * @param userId
	 * @return
	 */
	List<User> queryUserInfoByUserId(List<String> hashs);

//	/**
//	 * 根据组织id获取用户信息
//	 * @param orgId
//	 * @return
//	 */
//	List<User> queryUserInfoByOrgId(String orgId);
}
