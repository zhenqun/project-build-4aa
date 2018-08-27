package com.ido85.party.sso.security.authentication.dbsync.events;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

public class LoginLogEvent extends ApplicationContextEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;
	
	private String clientName;
	
	private String ip;
	
	private String message;
	
	private String type;
	
	public LoginLogEvent(ApplicationContext source) {
		super(source);
	}

	public LoginLogEvent(ApplicationContext source,String username,
			String clientName,String ip,String message,String type) {
		super(source);
		this.username = username;
		this.clientName = clientName;
		this.ip = ip;
		this.message = message;
		this.type = type;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

}
