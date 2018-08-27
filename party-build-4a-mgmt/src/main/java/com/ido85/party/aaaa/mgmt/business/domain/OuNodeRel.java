package com.ido85.party.aaaa.mgmt.business.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
@Entity
@Table(name="tf_f_ou_node_rel")
@Data
public class OuNodeRel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long ouNodeId;
	
	private Long ouId;
	
	private String nodeOrgName;
	
	private String nodeOrgId;
	
	private String nodeOrgCode;
	
	@Column(name="create_by")
	protected String createBy;
	@Column(name="create_date")
	protected Date createDate;
	@Column(name="update_by")
	protected String updateBy; 
	@Column(name="update_date")
	protected Date updateDate;
	@Column(name="del_flag")
	protected String delFlag;
	
	@OneToOne(targetEntity=Ou.class)
	@JoinColumn(name="ou_id")
	private Ou ou;
}
