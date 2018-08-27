package com.ido85.party.sso.security.authentication.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


@Entity
@Table(name="r_4a_user_vpn")
@Data
public class UserVpnRel implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="user_vpn_id")
	private Long userVpnId;

	@Column(name="user_id")
	private String userId;
	
	@Column(name="vpn")
	private String vpn;
	
	@Column(name="ou")
	private String ou;
	
	@Column(name="create_date")
	private Date createDate;
}
