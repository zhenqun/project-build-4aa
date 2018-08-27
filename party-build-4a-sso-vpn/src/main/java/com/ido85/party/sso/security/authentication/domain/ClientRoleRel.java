package com.ido85.party.sso.security.authentication.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="r_4a_client_role_rel")
public class ClientRoleRel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="client_role_id")
	private Long clientRoleId;
	
	@Column(name="client_id")
	private String clientId;
	
	@Column(name="role_id")
	private Long roleId;
	
	@Column(name="type")
	private String type;

	public Long getClientRoleId() {
		return clientRoleId;
	}

	public void setClientRoleId(Long clientRoleId) {
		this.clientRoleId = clientRoleId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
