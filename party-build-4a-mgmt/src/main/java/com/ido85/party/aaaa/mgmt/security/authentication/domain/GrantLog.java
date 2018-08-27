package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by fire on 2017/2/28.
 */
@Entity
@Table(name = "tf_f_grant_log")
public class GrantLog implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long grantLogId;
	private Date createDate;
	private String operateType;
	private String grantIp;
	private String mac;
	private String hostName;
	private String remarks;
	private String businessName;
	private String operateRelName;
	private String operateOrgId;
	private String operateOrgName;
	private String operateBy;
	private String hasDetail;
	private String operateName;
	private String url;
	private String orgCode;
	@Basic
	@Column(name = "id_card")
	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	private String idCard;

	@Id
	@Column(name = "grant_log_id")
	public Long getGrantLogId() {
		return grantLogId;
	}

	public void setGrantLogId(Long grantLogId) {
		this.grantLogId = grantLogId;
	}

	@Basic
	@Column(name = "create_date")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Basic
	@Column(name = "operate_type")
	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	@Basic
	@Column(name = "operate_name")
	public String getOperateName() {
		return operateName;
	}

	public void setOperateName(String operateName) {
		this.operateName = operateName;
	}

	@Basic
	@Column(name = "grant_ip")
	public String getGrantIp() {
		return grantIp;
	}

	public void setGrantIp(String grantIp) {
		this.grantIp = grantIp;
	}

	@Basic
	@Column(name = "mac")
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	@Basic
	@Column(name = "host_name")
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Basic
	@Column(name = "remarks")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Basic
	@Column(name = "business_name")
	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	@Basic
	@Column(name = "operate_rel_name")
	public String getOperateRelName() {
		return operateRelName;
	}

	public void setOperateRelName(String operateRelName) {
		this.operateRelName = operateRelName;
	}

	@Basic
	@Column(name = "operate_org_id")
	public String getOperateOrgId() {
		return operateOrgId;
	}

	public void setOperateOrgId(String operateOrgId) {
		this.operateOrgId = operateOrgId;
	}

	@Basic
	@Column(name = "operate_org_name")
	public String getOperateOrgName() {
		return operateOrgName;
	}

	public void setOperateOrgName(String operateOrgName) {
		this.operateOrgName = operateOrgName;
	}

	@Basic
	@Column(name = "operate_by")
	public String getOperateBy() {
		return operateBy;
	}

	public void setOperateBy(String operateBy) {
		this.operateBy = operateBy;
	}

	@Basic
	@Column(name = "has_detail")
	public String getHasDetail() {
		return hasDetail;
	}

	public void setHasDetail(String hasDetail) {
		this.hasDetail = hasDetail;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GrantLog grantLog = (GrantLog) o;

		if (grantLogId != null ? !grantLogId.equals(grantLog.grantLogId) : grantLog.grantLogId != null) return false;
		if (createDate != null ? !createDate.equals(grantLog.createDate) : grantLog.createDate != null) return false;
		if (operateType != null ? !operateType.equals(grantLog.operateType) : grantLog.operateType != null)
			return false;
		if (grantIp != null ? !grantIp.equals(grantLog.grantIp) : grantLog.grantIp != null) return false;
		if (mac != null ? !mac.equals(grantLog.mac) : grantLog.mac != null) return false;
		if (hostName != null ? !hostName.equals(grantLog.hostName) : grantLog.hostName != null) return false;
		if (remarks != null ? !remarks.equals(grantLog.remarks) : grantLog.remarks != null) return false;
		if (businessName != null ? !businessName.equals(grantLog.businessName) : grantLog.businessName != null)
			return false;
		if (operateRelName != null ? !operateRelName.equals(grantLog.operateRelName) : grantLog.operateRelName != null)
			return false;
		if (operateOrgId != null ? !operateOrgId.equals(grantLog.operateOrgId) : grantLog.operateOrgId != null)
			return false;
		if (operateOrgName != null ? !operateOrgName.equals(grantLog.operateOrgName) : grantLog.operateOrgName != null)
			return false;
		if (operateBy != null ? !operateBy.equals(grantLog.operateBy) : grantLog.operateBy != null) return false;
		if (hasDetail != null ? !hasDetail.equals(grantLog.hasDetail) : grantLog.hasDetail != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = grantLogId != null ? grantLogId.hashCode() : 0;
		result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
		result = 31 * result + (operateType != null ? operateType.hashCode() : 0);
		result = 31 * result + (grantIp != null ? grantIp.hashCode() : 0);
		result = 31 * result + (mac != null ? mac.hashCode() : 0);
		result = 31 * result + (hostName != null ? hostName.hashCode() : 0);
		result = 31 * result + (remarks != null ? remarks.hashCode() : 0);
		result = 31 * result + (businessName != null ? businessName.hashCode() : 0);
		result = 31 * result + (operateRelName != null ? operateRelName.hashCode() : 0);
		result = 31 * result + (operateOrgId != null ? operateOrgId.hashCode() : 0);
		result = 31 * result + (operateOrgName != null ? operateOrgName.hashCode() : 0);
		result = 31 * result + (operateBy != null ? operateBy.hashCode() : 0);
		result = 31 * result + (hasDetail != null ? hasDetail.hashCode() : 0);
		return result;
	}

	@Basic
	@Column(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	@Basic
	@Column(name = "org_code")
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
}
