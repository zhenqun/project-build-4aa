package com.ido85.party.sso.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class AuthentiInfoParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> uniqueKey;
	
	private String relName;
}
