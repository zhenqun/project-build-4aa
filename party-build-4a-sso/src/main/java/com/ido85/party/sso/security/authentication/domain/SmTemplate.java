package com.ido85.party.sso.security.authentication.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "tf_f_sm_template")
@Data
public class SmTemplate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2387732424694657780L;

	@Id
	@Column(name = "sm_template_id")
	private Long smTemplateId;
	
	@Column(name = "sm_template_name")
	private String smTemplateName;
	
	@Column(name = "sm_template_content")
	private String smTemplateContent;
	
	@Column(name = "sm_template_type")
	private String smTemplateType;
	
	@Column(name = "sm_template_orgname")
	private String smTemplateOrgname;
	
	@Column(name="expire_minute")
	private String expireMinute;
}
