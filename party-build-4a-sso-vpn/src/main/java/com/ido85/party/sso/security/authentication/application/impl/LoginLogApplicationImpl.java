package com.ido85.party.sso.security.authentication.application.impl;


import javax.inject.Named;

import org.springframework.scheduling.annotation.Async;

import com.ido85.party.sso.security.authentication.application.LoginLogApplication;
import com.ido85.party.sso.security.authentication.dbsync.events.LoginLogEvent;
import com.ido85.party.sso.security.authentication.dbsync.spring.InstanceFactory;
import com.ido85.party.sso.security.constants.LogConstants;
@Named
public class LoginLogApplicationImpl implements LoginLogApplication {

	/**
	 * 增加登录日志
	 * @throws Exception 
	 */
	@Async
	public void addLoginLog(String ip,String clientName,String name,String message,String type) throws Exception {
		InstanceFactory.publishEvent(
				new LoginLogEvent(InstanceFactory.getApplicationContext(),name,clientName,ip,message,type));
	}
}
