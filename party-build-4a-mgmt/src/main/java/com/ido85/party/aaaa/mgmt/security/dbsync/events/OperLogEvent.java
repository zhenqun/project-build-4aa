package com.ido85.party.aaaa.mgmt.security.dbsync.events;

import java.io.Serializable;

import org.springframework.context.ApplicationEvent;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.GrantLog;

public abstract class OperLogEvent<T> extends ApplicationEvent implements Serializable {

	public OperLogEvent(Object source) {
		super(source);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GrantLog operLog;
	
	private T operData;
	
	private T oldOperData;
	
	public T getOperData() {
		return operData;
	}

	public GrantLog getOperLog() {
		return operLog;
	}

	public void setOperLog(GrantLog operLog) {
		this.operLog = operLog;
	}

	public void setOperData(T operData) {
		this.operData = operData;
	}

	public T getOldOperData() {
		return oldOperData;
	}

	public void setOldOperData(T oldOperData) {
		this.oldOperData = oldOperData;
	}
	
	
}
