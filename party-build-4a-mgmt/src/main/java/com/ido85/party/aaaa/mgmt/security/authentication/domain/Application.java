package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_4a_application")
public class Application implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4887971190592821202L;

	@Id
	@Column(name="application_id")
	private Long applicationId;
	
	@Column(name="application_name")
	private String applicationName;

	@Column(name="description")
	private String description;
	
	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
}
