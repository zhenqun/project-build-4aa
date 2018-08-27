package com.ido85.party.aaa.authentication.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="tf_f_retention")
@Data
public class Retention implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3624791484246942245L;

	@Id
	@Column(name="retention_id")
	private Long retentionId;
	
	@Column(name="id_card")
	private String idCard;
	
	@Column(name="retention_date")
	private Date retentionDate;
	
	@Column(name="rel_name")
	private String relName;

	@Column(name="result")
	private boolean result;
	
	@Column(name="content")
	private String content;
	
}
