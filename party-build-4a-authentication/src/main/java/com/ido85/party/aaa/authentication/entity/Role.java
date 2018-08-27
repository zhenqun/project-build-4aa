package com.ido85.party.aaa.authentication.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="t_4a_role")
@Data
public class Role implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	private String name;
	
	private String description;
	
	private String clientId;
	
}
