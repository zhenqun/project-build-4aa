package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="tf_f_password_info")
@Data
public class PasswordInfo implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="password_info_id")
	private Long passwordInfoId;
	
	@Column(name="user_id")
	private String userId;
	
	@Column(name="date")
	private Date date;
	
	@Column(name="old_password")
	private String oldPassword;
	
	@Column(name="new_password")
	private String newPassword;
}
