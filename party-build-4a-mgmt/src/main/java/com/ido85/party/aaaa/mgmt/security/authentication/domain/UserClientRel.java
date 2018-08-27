package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="r_4a_user_client")
@Data
public class UserClientRel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private Long id;
	@Column(name="user_id")
	private String userId;
	@Column(name="client_id")
	private String clientId;
	@Column(name="manage_id")
	private String manageId;
	@Column(name="manage_name")
	private String manageName;
	@Column(name="manage_code")
	private String manageCode;
}
