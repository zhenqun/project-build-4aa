package com.ido85.party.aaaa.mgmt.business.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="r_4a_client_role_rel")
@Data
public class ClientRoleRel implements Serializable{/**
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
}
