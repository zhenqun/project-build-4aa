package com.ido85.party.aaaa.mgmt.dto.role;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class RoleManageParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String roleId;
	
	@NotNull
	private String roleName;
	
	@NotNull
	private String clientId;
	
	@NotNull
	private String roleDescription;
	
	private List<Long> permissionIds;

	private String detail;
	
}

