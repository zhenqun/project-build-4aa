package com.ido85.party.sso.security.authentication.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

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
}
