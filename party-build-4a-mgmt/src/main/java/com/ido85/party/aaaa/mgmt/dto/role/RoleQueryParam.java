package com.ido85.party.aaaa.mgmt.dto.role;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class RoleQueryParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer pageNo;
	
	private Integer pageSize;
	
	@NotNull
	private String clientId;//应用id
	
	private String roleName;//角色名
	
	private String roleDescription;//角色描述
	
}
