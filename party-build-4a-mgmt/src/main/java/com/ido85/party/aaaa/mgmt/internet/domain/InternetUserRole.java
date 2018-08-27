package com.ido85.party.aaaa.mgmt.internet.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="r_4a_user_role")
@Data
public class InternetUserRole implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="rel_id")
	private Long relId;
	@Column(name="user_id")
	private String userId;
	@Column(name="role_id")
	private Long roleId;
	@Column(name="authentication_user_id")
	private String authenticationUserId;
	@Column(name="del_flag")
	private String delFlag;
	@Column(name="create_date")
	private Date createDate;
	@Column(name="del_date")
	private Date delDate;
}
