package com.ido85.party.aaaa.mgmt.business.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserRolePK implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1906628408929947485L;

	@Column(name="role_id")
	private Long roleId;
	
	@Column(name="user_id")
	private String userId;

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
