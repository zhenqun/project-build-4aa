package com.ido85.party.aaaa.mgmt.business.dto;

import java.io.Serializable;

import lombok.Data;
@Data
public class ClientRoleQueryParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5118460026317910093L;
	private String businessUserId;
	private String clientId;
}
