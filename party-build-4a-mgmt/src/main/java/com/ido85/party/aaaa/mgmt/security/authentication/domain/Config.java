package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.ido85.party.aaaa.mgmt.security.common.BaseEntity;

/**
 * The persistent class for the TF_F_CONFIG database table.
 * 
 */
@Entity
@Table(name="tf_f_config")
@Where(clause="DEL_FLAG='0'")
public class Config extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="config_id")
	private Long configId;

	@Column(name="config_code")
	private String configCode;

	@Column(name="config_type")
	private String configType;

	@Column(name="config_value")
	private String configValue;

	public Config() {
	}

	public Long getConfigId() {
		return this.configId;
	}

	public void setConfigId(Long configId) {
		this.configId = configId;
	}

	public String getConfigCode() {
		return this.configCode;
	}

	public void setConfigCode(String configCode) {
		this.configCode = configCode;
	}

	public String getConfigType() {
		return this.configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}

	public String getConfigValue() {
		return this.configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

}