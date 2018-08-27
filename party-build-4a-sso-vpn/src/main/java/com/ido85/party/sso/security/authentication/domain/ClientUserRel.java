package com.ido85.party.sso.security.authentication.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="r_4a_client_user_rel")
public class ClientUserRel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3624791484246942245L;

	@Id
	@Column(name="client_user_rel_id")
	private Long clientUserRelId;
	
	@Column(name="client_id")
	private String clientId;
	
	@Column(name="user_id")
	private String userId;

	public Long getClientUserRelId() {
		return clientUserRelId;
	}

	public void setClientUserRelId(Long clientUserRelId) {
		this.clientUserRelId = clientUserRelId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
