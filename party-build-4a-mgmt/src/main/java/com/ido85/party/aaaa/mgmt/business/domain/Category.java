package com.ido85.party.aaaa.mgmt.business.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;

@Data
@Entity
@Table(name="t_4a_category")
public class Category implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="category_id")
	private Long categoryId;
	
	@Column(name="category_name")
	private String categoryName;
	
	@Column(name="category_description")
	private String categoryDescription;
	
	@Column(name="del_flag")
	private String delFlag;
	
	@Column(name="client_id")
	private String clientId;
	
	@ManyToMany(targetEntity = Permission.class, fetch = FetchType.EAGER)
    @JoinTable(name = "r_4a_permission_category", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<Permission> permissions;
}
