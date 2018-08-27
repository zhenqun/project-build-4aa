package com.ido85.party.aaaa.mgmt.business.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name="r_4a_user_vpn")
@Data
public class BusinessUserVpnRel implements Serializable{/**
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

	@Column(name="flag")
	private String flag;
}
