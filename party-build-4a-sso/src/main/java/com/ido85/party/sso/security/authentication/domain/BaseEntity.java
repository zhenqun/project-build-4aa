package com.ido85.party.sso.security.authentication.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 实体的基类
 */
@MappedSuperclass
public class BaseEntity  implements Serializable{
	private static final long serialVersionUID = 1L;
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
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	

}
