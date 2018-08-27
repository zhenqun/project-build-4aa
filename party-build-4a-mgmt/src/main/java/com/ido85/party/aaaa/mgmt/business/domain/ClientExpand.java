package com.ido85.party.aaaa.mgmt.business.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="client_expand")
@Data
public class ClientExpand implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private Long id;
	@Column(name="client_id")
	private String clientId;
	@Column(name="client_name")
	private String clientName;
	@Column(name="is_display")
	private boolean isDisplay;
	@Column(name="level_url")
	private String levelUrl;
	@Column(name="level_type")
	private String levelType;
	@Column(name="level_name")
	private String levelName;
	@Column(name="check_url")
	private String checkUrl;
	@Column(name="order")
	private String order;
	@Column(name="is_eureka")
	private String isEureka;
	@Column(name="search_url")
	private String searchUrl;
	@Column(name="create_role")
	private String createRole;
}
