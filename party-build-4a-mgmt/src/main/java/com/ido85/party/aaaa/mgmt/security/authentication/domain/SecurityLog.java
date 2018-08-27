package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

@Entity
@Table(name = "tl_b_log")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries(value = {})
public class SecurityLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "log_id")
	private Long logId;
	@Column(name = "user_id")
	private String userId;
	@Column(name = "username")
	private String username;
	@Column(name = "mobile")
	private String mobile;
	@Column(name = "log_type")
	private String logType;
	@Column(name = "old_detail")
	private String oldDetail;
	@Column(name = "detail")
	private String detail;
	@Column(name = "nickname")
	private String nickName;
	@Column(name = "id_card")
	private String idCard;

	@Column(name = "rel_name")
	private String relName;
	@Column(name = "create_by")
	private String createBy;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "update_by")
	private String updateBy;

	@Column(name = "update_date")
	private Date updateDate;

	@Column(name="org_id")
	private String orgId;
	
	@Column(name="org_name")
	private String orgName;
	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getRelName() {
		return relName;
	}

	public void setRelName(String relName) {
		this.relName = relName;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getOldDetail() {
		return oldDetail;
	}

	public void setOldDetail(String oldDetail) {
		this.oldDetail = oldDetail;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

}
