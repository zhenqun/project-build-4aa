package com.ido85.party.sso.security.authentication.application;


public interface LoginLogApplication {

	/**
	 * 增加登录日志
	 * @param request
	 * @param user
	 * @throws Exception 
	 */
	void addLoginLog(String ip,String clientName,String username,String message,String type) throws Exception;

}
