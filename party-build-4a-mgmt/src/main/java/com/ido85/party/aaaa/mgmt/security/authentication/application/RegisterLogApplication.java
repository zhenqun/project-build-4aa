package com.ido85.party.aaaa.mgmt.security.authentication.application;

import javax.servlet.http.HttpServletRequest;

public interface RegisterLogApplication {

	/**
	 * 增加注册日志
	 * @param request
	 * @param newUser
	 * @throws Exception 
	 */
	void addRegisterLog(HttpServletRequest request,String username,String orgName,String orgId,String orgCode,String name,String idCard,String userId,String message,String type,String typeName) throws Exception;

}
