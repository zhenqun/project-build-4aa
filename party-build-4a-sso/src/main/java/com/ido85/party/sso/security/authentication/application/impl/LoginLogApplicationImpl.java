package com.ido85.party.sso.security.authentication.application.impl;


import javax.inject.Named;

import org.springframework.scheduling.annotation.Async;

import com.ido85.party.sso.security.authentication.application.LoginLogApplication;
import com.ido85.party.sso.security.authentication.dbsync.events.LoginLogEvent;
import com.ido85.party.sso.security.authentication.dbsync.spring.InstanceFactory;
@Named
public class LoginLogApplicationImpl implements LoginLogApplication {

	/**
	 * 增加登录日志
	 * @throws Exception
	 */
	/**  这里不清楚为什么使用spring消息发布与订阅的方式处理数据**/

	@Async
	public void addLoginLog(String ip,String hostname,String name,String message,String type) throws Exception {
		InstanceFactory.publishEvent(
				new LoginLogEvent(InstanceFactory.getApplicationContext(),name,hostname,ip,message,type));
	}
	
}
