package com.ido85.party.aaaa.mgmt.security.authentication.application.impl;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.springframework.scheduling.annotation.Async;

import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.security.authentication.application.LoginLogApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.LoginLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.LoginLogEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.spring.InstanceFactory;
import com.ido85.party.aaaa.mgmt.security.utils.IPHostUtils;
import com.ido85.party.aaaa.mgmt.security.utils.SystemUtils;


@Named
public class LoginLogApplicationImpl implements LoginLogApplication {

	@Inject
	private IdGenerator idGenerator;
	
//	@Inject
//	private ClientRepository clientRepository;
	
	/**
	 * 增加登录日志
	 * @throws Exception 
	 */
	@Async
	public void addLoginLog(String ip,String hostname,String name,String message,String type) throws Exception {
		InstanceFactory.publishEvent(
				new LoginLogEvent(InstanceFactory.getApplicationContext(),name,hostname,ip,message,type));
	}
	
}
