package com.ido85.party.sso.security.authentication.domain;

import lombok.Data;

import javax.persistence.Column;
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
	@Column(name="authentication_info_id")
	private Long authenticationInfoId;
	@Column(name="unique_key")
	private String uniqueKey;
	@Column(name="rel_name")
	private String relName;
	@Column(name="create_date")
	private Date createDate;
	@Column(name="client_id")
	private String clientId;
	@Column(name="client_name")
	private String clientName;
	@Column(name="del_flag")
	private String delFlag;
	@Column(name="role_id")
	private Long roleId;
	@Column(name="user_id")
	private String userId;
	@Column(name="id_card")
	private String idCard;
	@Column(name="valid_flag")
	private String validFlag;

}
