package com.ido85.party.sso.security.authentication.dbsync.listener;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;

import com.ido85.party.sso.distribute.generator.IdGenerator;
import com.ido85.party.sso.security.authentication.application.UserApplication;
import com.ido85.party.sso.security.authentication.dbsync.events.LoginLogEvent;
import com.ido85.party.sso.security.authentication.domain.LoginLog;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.repository.LoginLogResources;
import com.ido85.party.sso.security.utils.SystemUtils;
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
		String clientName = event.getClientName();
		String message = event.getMessage();
		String ip = event.getIp();
		String type = event.getType();
		LoginLog loginLog = new LoginLog();
		loginLog.setClientName(clientName);
		loginLog.setLogId(idGenerator.next());
		loginLog.setLoginDate(new Date());
		loginLog.setLoginIp(ip);
		loginLog.setRemarks(message);
		User user = userApp.getUserByUserName(username);
		if(null != user){
			loginLog.setLoginName(user.getUsername());
			loginLog.setLoginResult(message);
			loginLog.setIdCard(user.getIdCard());
			loginLog.setRelName(user.getName());
			loginLog.setUserId(user.getId());
		}
		loginLog.setLoginType(type);
		userApp.updateLastLoginDate(username);
		loginLogResources.save(loginLog);
	}

}
