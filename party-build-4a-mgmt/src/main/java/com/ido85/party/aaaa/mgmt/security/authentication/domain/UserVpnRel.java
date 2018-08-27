package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@Table(name="r_4a_user_vpn")
public class UserVpnRel implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="user_ou_id")
	private Long userOuId;
	
	@Column(name="ou_name")
	private String ouName;
	
	@Column(name="vpn")
	private String vpn;
	
	@Column(name="user_id")
	private String userId;

	@Column(name="flag")
	private String flag;
}
