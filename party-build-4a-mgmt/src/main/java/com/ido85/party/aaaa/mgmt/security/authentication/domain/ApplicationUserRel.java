package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="r_4a_application_user_rel")
public class ApplicationUserRel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3624791484246942245L;

	@Id
	@Column(name="application_user_rel_id")
	private Long applicationUserId;
	
	@Column(name="application_id")
	private Long applicationId;
	
	@Column(name="user_id")
	private Long userId;

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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
