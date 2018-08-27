package com.ido85.party.aaaa.mgmt.business.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Entity
@Table(name="tf_f_ou")
@Data
public class Ou implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String ouId;
	
	private String ouName;
	
	private String ouDescription;
	
	private String fullDescription;
}
