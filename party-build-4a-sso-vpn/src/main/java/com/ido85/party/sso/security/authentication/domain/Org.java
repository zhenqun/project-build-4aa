package com.ido85.party.sso.security.authentication.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

@Entity
@Table(name = "tf_f_org")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries(value = {})
public class Org implements Serializable {
	@Id
	@Column(name="id")
	private String id;
	@Column(name="org_name")
	private String orgName;
	@Column(name="code")
	private String code;
	@Column(name="parent_id")
	private String parentId;
	@Column(name="order_id")
	private Integer orderId;
	@Column(name="del_flag")
	private Integer delFlag;
	@Column(name="has_children")
	private Integer hasChildren;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Integer getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
	public Integer getHasChildren() {
		return hasChildren;
	}
	public void setHasChildren(Integer hasChildren) {
		this.hasChildren = hasChildren;
	}
}
