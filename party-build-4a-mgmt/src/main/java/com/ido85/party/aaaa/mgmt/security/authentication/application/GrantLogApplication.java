package com.ido85.party.aaaa.mgmt.security.authentication.application;


import javax.servlet.http.HttpServletRequest;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.GrantLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;

public interface GrantLogApplication {

	/**
	 * 增加日志
	 * @param <T>
	 * @param request
	 * @param addAuditor
	 * @param addAuditorName
	 * @param i
	 * @param string
	 * @return 
	 * @throws Exception 
	 */
	<T> void addLog(User currentUser,GrantLog grantLog, String addAuditor, String addAuditorName, String hasDetail, String string,T detail,T oldDetail) throws Exception;

}
