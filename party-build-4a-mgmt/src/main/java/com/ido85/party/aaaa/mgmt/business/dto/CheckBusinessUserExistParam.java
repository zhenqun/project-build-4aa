package com.ido85.party.aaaa.mgmt.business.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
@Data
public class CheckBusinessUserExistParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String clientId;
	
	private List<CheckBusinessUserParam> users;
}
