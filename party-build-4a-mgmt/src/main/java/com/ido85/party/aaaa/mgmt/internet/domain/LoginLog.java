package com.ido85.party.aaaa.mgmt.internet.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by fire on 2017/2/28.
 */
@Entity
@Table(name = "tf_f_login_log")
public class LoginLog implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long logId;
	private String loginName;
	private Date loginDate;
	private String loginIp;
	private String loginType;
	private String mac;
	private String loginResult;
	private String hostName;
	private String orgName;
	private String relName;
	private String idCard;
	private String orgId;
	private String userId;
	private String remarks;

	@Id
	@Column(name = "log_id")
	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	@Basic
	@Column(name = "login_name")
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Basic
	@Column(name = "login_date")
	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	@Basic
	@Column(name = "login_ip")
	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	@Basic
	@Column(name = "login_type")
	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
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
	@Column(name = "login_result")
	public String getLoginResult() {
		return loginResult;
	}

	public void setLoginResult(String loginResult) {
		this.loginResult = loginResult;
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
	@Column(name = "org_name")
	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	@Basic
	@Column(name = "rel_name")
	public String getRelName() {
		return relName;
	}

	public void setRelName(String relName) {
		this.relName = relName;
	}

	@Basic
	@Column(name = "id_card")
	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	@Basic
	@Column(name = "org_id")
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	@Basic
	@Column(name = "user_id")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LoginLog loginLog = (LoginLog) o;

		if (logId != null ? !logId.equals(loginLog.logId) : loginLog.logId != null) return false;
		if (loginName != null ? !loginName.equals(loginLog.loginName) : loginLog.loginName != null) return false;
		if (loginDate != null ? !loginDate.equals(loginLog.loginDate) : loginLog.loginDate != null) return false;
		if (loginIp != null ? !loginIp.equals(loginLog.loginIp) : loginLog.loginIp != null) return false;
		if (loginType != null ? !loginType.equals(loginLog.loginType) : loginLog.loginType != null) return false;
		if (mac != null ? !mac.equals(loginLog.mac) : loginLog.mac != null) return false;
		if (loginResult != null ? !loginResult.equals(loginLog.loginResult) : loginLog.loginResult != null)
			return false;
		if (hostName != null ? !hostName.equals(loginLog.hostName) : loginLog.hostName != null) return false;
		if (orgName != null ? !orgName.equals(loginLog.orgName) : loginLog.orgName != null) return false;
		if (relName != null ? !relName.equals(loginLog.relName) : loginLog.relName != null) return false;
		if (idCard != null ? !idCard.equals(loginLog.idCard) : loginLog.idCard != null) return false;
		if (orgId != null ? !orgId.equals(loginLog.orgId) : loginLog.orgId != null) return false;
		if (userId != null ? !userId.equals(loginLog.userId) : loginLog.userId != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = logId != null ? logId.hashCode() : 0;
		result = 31 * result + (loginName != null ? loginName.hashCode() : 0);
		result = 31 * result + (loginDate != null ? loginDate.hashCode() : 0);
		result = 31 * result + (loginIp != null ? loginIp.hashCode() : 0);
		result = 31 * result + (loginType != null ? loginType.hashCode() : 0);
		result = 31 * result + (mac != null ? mac.hashCode() : 0);
		result = 31 * result + (loginResult != null ? loginResult.hashCode() : 0);
		result = 31 * result + (hostName != null ? hostName.hashCode() : 0);
		result = 31 * result + (orgName != null ? orgName.hashCode() : 0);
		result = 31 * result + (relName != null ? relName.hashCode() : 0);
		result = 31 * result + (idCard != null ? idCard.hashCode() : 0);
		result = 31 * result + (orgId != null ? orgId.hashCode() : 0);
		result = 31 * result + (userId != null ? userId.hashCode() : 0);
		return result;
	}

	@Basic
	@Column(name = "remarks")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
