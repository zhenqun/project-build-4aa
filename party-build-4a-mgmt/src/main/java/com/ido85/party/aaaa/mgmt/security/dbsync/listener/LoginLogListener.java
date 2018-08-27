package com.ido85.party.aaaa.mgmt.security.dbsync.listener;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;

import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.security.authentication.application.UserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.LoginLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.LoginLogResources;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.LoginLogEvent;
import com.ido85.party.aaaa.mgmt.security.utils.SystemUtils;

@Named
public class LoginLogListener implements ApplicationListener<LoginLogEvent>{

	@Inject
	private LoginLogResources loginLogResources;
	
	@Inject
	private UserApplication userApp;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Override
	@Transactional
	@Async
	public void onApplicationEvent(LoginLogEvent event) {
		String username = event.getUsername();
		String hostname = event.getHostname();
		String message = event.getMessage();
		String ip = event.getIp();
		String type = event.getType();
		LoginLog loginLog = new LoginLog();
		loginLog.setHostName(hostname);
		loginLog.setLogId(idGenerator.next());
		loginLog.setLoginDate(new Date());
		loginLog.setLoginIp(ip);
		loginLog.setMac(SystemUtils.getMacAddress(ip));
		loginLog.setRemarks(message);
		User user = userApp.getUserByUserName(username);
		if(null != user){
			loginLog.setLoginName(user.getUsername());
			loginLog.setLoginResult(message);
			loginLog.setIdCard(user.getIdCard());
//			loginLog.setOrgName(user.getOrgName());
			loginLog.setRelName(user.getName());
			loginLog.setUserId(user.getId());
//			loginLog.setOrgId(user.getOrgId());
//			loginLog.setOrgCode(user.getOrgCode());
		}
		loginLog.setLoginType(type);
		userApp.updateLastLoginDate(username);
		loginLogResources.save(loginLog);
	}

}
