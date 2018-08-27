package com.ido85.party.sso.security.authentication.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="t_4a_application")
@Data
public class Application implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="application_id")
	private Long applicationId; 
	
	@Column(name="application_name")
	private String applicationName;
	
	@Column(name="application_url")
	private String applicationUrl;
	
	@Column(name="application_image")
	private String applicationImage;
	
	@Column(name="order")
	private String order;
	
	@Column(name="category_id")
	private Long categoryId;
	
	@Column(name="del_flag")
	private String delFlag;
	
	@Column(name="is_dock")
	private boolean isDock;
	
	@Column(name="client_id")
	private String clientId;
	
	@Column(name="ou_name")
	private String ouName;
	
	@Column(name="have_node")
	private String haveNode;
	
	@OneToOne
	@JoinColumn(name="category_id",insertable=false,updatable=false)
	private ApplicationCategory category;
}
