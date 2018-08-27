/**
 * 
 */
package com.ido85.party.aaaa.mgmt.business.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author rongxj
 *
 */
@Entity
@Table(name = "oauth_client_details")
@Data
public class PlatformClientDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 101639926601960704L;

	@Id
	@Column(name = "client_id")
	private String clientId;
	@Column(name="client_name")
	private String clientName;
	@Column(name="is_display")
	private boolean isDisplay;
	@Column(name="level_url")
	private String levelUrl;
}
