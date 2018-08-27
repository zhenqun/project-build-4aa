package com.ido85.party.sso.security.authentication.dbsync.events;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

import com.ido85.party.sso.security.authentication.domain.RegisterLog;

public class RegisterLogEvent extends ApplicationContextEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private RegisterLog registerLog;

	public RegisterLogEvent(ApplicationContext source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	public RegisterLogEvent(ApplicationContext source, RegisterLog registerLog) {
		super(source);
		this.registerLog = registerLog;
	}

	public RegisterLog getRegisterLog() {
		return registerLog;
	}

	public void setRegisterLog(RegisterLog registerLog) {
		this.registerLog = registerLog;
	}

}
