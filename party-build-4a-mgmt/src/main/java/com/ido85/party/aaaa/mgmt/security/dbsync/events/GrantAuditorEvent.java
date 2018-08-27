package com.ido85.party.aaaa.mgmt.security.dbsync.events;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.GrantLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.LogDetail;

public class GrantAuditorEvent extends ApplicationContextEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4048511954864135040L;
	
	private GrantLog grantLog;
	
	private List<LogDetail> LogDetailsList;
	
	public GrantAuditorEvent(ApplicationContext source) {
		super(source);
	}

	public GrantAuditorEvent(ApplicationContext source, GrantLog grantLog) {
		super(source);
		this.grantLog = grantLog;
	}

	public GrantAuditorEvent(ApplicationContext source, List<LogDetail> LogDetailsList) {
		super(source);
		this.LogDetailsList = LogDetailsList;
	}

	public GrantLog getGrantLog() {
		return grantLog;
	}

	public void setGrantLog(GrantLog grantLog) {
		this.grantLog = grantLog;
	}

	public List<LogDetail> getLogDetailsList() {
		return LogDetailsList;
	}

	public void setLogDetailsList(List<LogDetail> logDetailsList) {
		LogDetailsList = logDetailsList;
	}
	
}
