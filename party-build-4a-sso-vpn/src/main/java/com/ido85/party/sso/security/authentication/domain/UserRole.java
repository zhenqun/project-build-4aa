package com.ido85.party.sso.security.authentication.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name="r_4a_user_role")
@Data
public class UserRole implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8190614678385528577L;

	@Id
	@JsonIgnore
	private Long id;
	@JsonIgnore
	@Column(name="user_id")
	private String userId;
	@JsonIgnore
	@Column(name="role_id")
	private Long roleId;

	@Column(name="manage_id")
	private String manageId;
	
	@Column(name="manage_name")
	private String manageName;
	
	@Column(name="manage_code")
	private String manageCode;
	
	@OneToOne
	@JoinColumn(name="role_id",insertable=false,updatable=false)
	private Role role;
}
