package com.ido85.party.sso.security.authentication.application.impl;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.ido85.party.sso.distribute.generator.IdGenerator;
import com.ido85.party.sso.security.authentication.application.RegisterLogApplication;
import com.ido85.party.sso.security.authentication.dbsync.events.RegisterLogEvent;
import com.ido85.party.sso.security.authentication.dbsync.spring.InstanceFactory;
import com.ido85.party.sso.security.authentication.domain.RegisterLog;
import com.ido85.party.sso.security.utils.IPHostUtils;
@Named
public class RegisterLogApplicationImpl implements RegisterLogApplication {

	@Inject
	private IdGenerator idGenerator;
	
	/**
	 * 增加注册日志
	 * @throws Exception 
	 */
	public void addRegisterLog(HttpServletRequest request,String username,String orgName,String orgId,String orgCode,
			String name,String idCard,String userId,String message,String type,String typeName) throws Exception {
		RegisterLog registerLog = new RegisterLog();
		String ip = IPHostUtils.getRemoteHost(request);
		registerLog.setHostName(IPHostUtils.getRemoteHost(request));
		registerLog.setMac(IPHostUtils.getMACAddress(ip));
		registerLog.setRegisterDate(new Date());
		registerLog.setRegisterIp(request.getLocalAddr());
		registerLog.setRegisterLogId(idGenerator.next());
		registerLog.setRegisterType(type);
		registerLog.setRemarks(message);
		registerLog.setUserName(username);
		registerLog.setOrgName(orgName);
		registerLog.setRelName(name);
		registerLog.setIdCard(idCard);
		registerLog.setOrgId(orgId);
		registerLog.setUserId(userId);
		registerLog.setRegisterTypeName(typeName);
		registerLog.setOrgCode(orgCode);
		InstanceFactory.publishEvent(
				new RegisterLogEvent(InstanceFactory.getApplicationContext(), registerLog));
	}
	
}
