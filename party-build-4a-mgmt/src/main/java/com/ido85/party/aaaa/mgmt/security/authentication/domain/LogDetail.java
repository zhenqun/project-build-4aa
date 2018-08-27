package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by fire on 2017/2/28.
 */
@Entity
@Table(name = "tf_f_log_detail")
public class LogDetail implements Serializable {
	private Long logDetailId;
	private String originValue;
	private String currentValue;
	private Long logId;
	private String tableName;
	private String columnName;
	private String remarks;
	private String grantUserId;
	private String grantRelName;
	private String grantOrgId;
	private String grantOrgName;
	@Id
	@Column(name = "log_detail_id")
	public Long getLogDetailId() {
		return logDetailId;
	}

	public void setLogDetailId(Long logDetailId) {
		this.logDetailId = logDetailId;
	}

	@Basic
	@Column(name = "origin_value")
	public String getOriginValue() {
		return originValue;
	}

	public void setOriginValue(String originValue) {
		this.originValue = originValue;
	}

	@Basic
	@Column(name = "current_value")
	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	@Basic
	@Column(name = "log_id")
	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	@Basic
	@Column(name = "table_name")
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Basic
	@Column(name = "column_name")
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
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
	@Column(name = "grant_user_id")
	public String getGrantUserId() {
		return grantUserId;
	}

	public void setGrantUserId(String grantUserId) {
		this.grantUserId = grantUserId;
	}

	@Basic
	@Column(name = "grant_rel_name")
	public String getGrantRelName() {
		return grantRelName;
	}

	public void setGrantRelName(String grantRelName) {
		this.grantRelName = grantRelName;
	}

	@Basic
	@Column(name = "grant_org_id")
	public String getGrantOrgId() {
		return grantOrgId;
	}

	public void setGrantOrgId(String grantOrgId) {
		this.grantOrgId = grantOrgId;
	}

	@Basic
	@Column(name = "grant_org_name")
	public String getGrantOrgName() {
		return grantOrgName;
	}

	public void setGrantOrgName(String grantOrgName) {
		this.grantOrgName = grantOrgName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LogDetail that = (LogDetail) o;

		if (logDetailId != null ? !logDetailId.equals(that.logDetailId) : that.logDetailId != null) return false;
		if (originValue != null ? !originValue.equals(that.originValue) : that.originValue != null) return false;
		if (currentValue != null ? !currentValue.equals(that.currentValue) : that.currentValue != null) return false;
		if (logId != null ? !logId.equals(that.logId) : that.logId != null) return false;
		if (tableName != null ? !tableName.equals(that.tableName) : that.tableName != null) return false;
		if (columnName != null ? !columnName.equals(that.columnName) : that.columnName != null) return false;
		if (remarks != null ? !remarks.equals(that.remarks) : that.remarks != null) return false;
		if (grantUserId != null ? !grantUserId.equals(that.grantUserId) : that.grantUserId != null) return false;
		if (grantRelName != null ? !grantRelName.equals(that.grantRelName) : that.grantRelName != null) return false;
		if (grantOrgId != null ? !grantOrgId.equals(that.grantOrgId) : that.grantOrgId != null) return false;
		if (grantOrgName != null ? !grantOrgName.equals(that.grantOrgName) : that.grantOrgName != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = logDetailId != null ? logDetailId.hashCode() : 0;
		result = 31 * result + (originValue != null ? originValue.hashCode() : 0);
		result = 31 * result + (currentValue != null ? currentValue.hashCode() : 0);
		result = 31 * result + (logId != null ? logId.hashCode() : 0);
		result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
		result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
		result = 31 * result + (remarks != null ? remarks.hashCode() : 0);
		result = 31 * result + (grantUserId != null ? grantUserId.hashCode() : 0);
		result = 31 * result + (grantRelName != null ? grantRelName.hashCode() : 0);
		result = 31 * result + (grantOrgId != null ? grantOrgId.hashCode() : 0);
		result = 31 * result + (grantOrgName != null ? grantOrgName.hashCode() : 0);
		return result;
	}

}
