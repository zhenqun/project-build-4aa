package com.ido85.party.sso.security.authentication.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import lombok.Data;

@Entity
@Table(name="t_4a_application_category")
@Data
public class ApplicationCategory implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="category_id")
	private Long categoryId;

	@Column(name="category_name")
	private String categoryName;
	
	@Column(name="order")
	private String order;
	
	@Column(name="del_flag")
	private String delFlag;
	
	@Column(name="category_type")
	private String type;
	
//	@OneToMany(targetEntity=Application.class,fetch = FetchType.EAGER)
//	@JoinColumn(name="category_id")
//	@Where(clause="del_flag='0'")
//	private List<Application> apps;
}
