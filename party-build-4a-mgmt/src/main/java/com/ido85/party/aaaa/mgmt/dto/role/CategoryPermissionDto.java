package com.ido85.party.aaaa.mgmt.dto.role;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class CategoryPermissionDto implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String categoryId;
	
	private String categoryName;
	
	private List<PermissionQueryDto> permissions;

}
