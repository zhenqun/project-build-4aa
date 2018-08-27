package com.ido85.party.aaaa.mgmt.internet.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="r_4a_application_role_rel")
public class ApplicationInternetRoleRel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1259500569680710081L;

	@Id
	@Column(name="application_role_id")
	private Long applicationUserId;
	
	@Column(name="application_id")
	private Long applicationId;
	
	@Column(name="role_id")
	private Integer roleId;
	
	@Column(name="type")
	private String type;

	public Long getApplicationUserId() {
		return applicationUserId;
	}

	public void setApplicationUserId(Long applicationUserId) {
		this.applicationUserId = applicationUserId;
	}

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

}
