package com.ido85.party.sso.security.authentication.dbsync.events;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

public class LoginLogEvent extends ApplicationContextEvent {
	/**
	 *  这里定义了继承了ApplicationContextEvent  相当于定义了自己了一个监听事件
	 *   这必须实现自己的构造方法。
	 */
	private static final long serialVersionUID = 1L;

	private String username;
	
	private String hostname;
	
	private String ip;
	
	private String message;
	
	private String type;
	
	public LoginLogEvent(ApplicationContext source) {
		super(source);
	}

	public LoginLogEvent(ApplicationContext source,String username,
			String hostname,String ip,String message,String type) {
		super(source);
		this.username = username;
		this.hostname = hostname;
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

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
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

}
