package com.ido85.party.aaaa.mgmt.business.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Entity
@Table(name="t_4a_permission")
@Data
public class Permission implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="permission_id")
	private Long permissionId;
	
	@Column(name="permission_name")
	private String permissionName;
	
	@Column(name="permission_description")
	private String permissionDescription;
	
	@Column(name="manage_id")
	private String manageId;
	
	@Column(name="manage_name")
	private String manageName;
	
	@Column(name="client_id")
	private String clientId;
	
	@Column(name="is_limit")
	private boolean isLimit;
	

}
