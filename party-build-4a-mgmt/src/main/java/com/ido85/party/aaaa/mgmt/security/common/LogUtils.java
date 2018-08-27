package com.ido85.party.aaaa.mgmt.security.common;


import javax.servlet.http.HttpServletRequest;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.GrantLog;
import com.ido85.party.aaaa.mgmt.security.utils.IPHostUtils;

public class LogUtils {
	
	/**
	 * 获取ip mac hostname
	 * @param request
	 * @return
	 */
	public static GrantLog CreateGrantLog(HttpServletRequest request) {
		GrantLog grantLog = new GrantLog();
		String ip = IPHostUtils.getRemoteHost(request);
		grantLog.setGrantIp(ip);
		grantLog.setHostName(request.getRemoteHost());
		try {
			grantLog.setMac(IPHostUtils.getMACAddress(ip));
		} catch (Exception e) {
			grantLog.setMac(null);
		}
		return grantLog;
	}
}
