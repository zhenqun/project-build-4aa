package com.ido85.party.sso.security.authentication.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tf_f_retention")
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
	
	public Long getRetentionId() {
		return retentionId;
	}

	public void setRetentionId(Long retentionId) {
		this.retentionId = retentionId;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public Date getRetentionDate() {
		return retentionDate;
	}

	public void setRetentionDate(Date retentionDate) {
		this.retentionDate = retentionDate;
	}

	public String getRelName() {
		return relName;
	}

	public void setRelName(String relName) {
		this.relName = relName;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
