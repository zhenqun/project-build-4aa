package com.ido85.party.sso.security.authentication.dbsync.listener;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;

import com.ido85.party.sso.security.authentication.dbsync.events.RegisterLogEvent;
import com.ido85.party.sso.security.authentication.domain.RegisterLog;
import com.ido85.party.sso.security.authentication.repository.RegisterLogResources;
@Named
public class RegisterLogListener implements ApplicationListener<RegisterLogEvent>{

	@Inject
	private RegisterLogResources registerLogResources;
	
	@Transactional
	@Async
	public void onApplicationEvent(RegisterLogEvent event) {
		RegisterLog registerLog = event.getRegisterLog();
		registerLogResources.save(registerLog);
	}

}
