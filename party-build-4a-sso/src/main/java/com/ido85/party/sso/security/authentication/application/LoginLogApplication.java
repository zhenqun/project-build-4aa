package com.ido85.party.sso.security.authentication.application;


import javax.servlet.http.HttpServletRequest;

import com.ido85.party.sso.security.authentication.domain.User;

public interface LoginLogApplication {

	/**
	 * 增加登录日志
	 * @param request
	 * @param user
	 * @throws Exception 
	 */
	void addLoginLog(String ip,String hostname,String username,String message,String type) throws Exception;

}
