package com.ido85.party.aaa.authentication.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="tf_f_authentication_info")
@Data
public class AuthenticationInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long authenticationInfoId;
	
	private String uniqueKey;
	
	private String relName;
	
	private Date createDate;
	
	private String clientId;
	
	private String clientName;
	
	private String delFlag;
	
	private Long roleId;
	
	private String userId;

	private String validFlag;

	private String idCard;

}
