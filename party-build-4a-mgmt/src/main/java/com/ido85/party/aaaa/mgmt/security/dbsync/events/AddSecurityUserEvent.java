package com.ido85.party.aaaa.mgmt.security.dbsync.events;

import java.util.List;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;

public class AddSecurityUserEvent extends OperLogEvent<List<User>>{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8424823529445708311L;

	public AddSecurityUserEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	
	
}
