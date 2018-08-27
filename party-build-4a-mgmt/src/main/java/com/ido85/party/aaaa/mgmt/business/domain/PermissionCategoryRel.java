package com.ido85.party.aaaa.mgmt.business.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="r_4a_permission_category")
public class PermissionCategoryRel implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="permission_category_rel_id")
	private Long permissionCategoryRelId;
	
	@Column(name="category_id")
	private Long categoryId;
	
	@Column(name="permission_id")
	private Long permissionId;

}
