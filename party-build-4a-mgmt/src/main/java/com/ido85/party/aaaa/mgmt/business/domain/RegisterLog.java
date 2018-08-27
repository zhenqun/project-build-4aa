package com.ido85.party.aaaa.mgmt.business.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by fire on 2017/2/28.
 */
@Data
@Entity
@Table(name = "tf_f_register_log")
public class RegisterLog implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "register_log_id")
	private Long registerLogId;
	@Column(name = "user_name")
	private String userName;
	@Column(name = "register_date")
	private Date registerDate;
	@Column(name = "register_type")
	private String registerType;
	@Column(name = "register_type_name")
	private String registerTypeName;
	@Column(name = "register_ip")
	private String registerIp;
	@Column(name = "mac")
	private String mac;
	@Column(name = "host_name")
	private String hostName;
	@Column(name = "remarks")
	private String remarks;
	@Column(name = "org_name")
	private String orgName;
	@Column(name = "rel_name")
	private String relName;
	@Column(name = "id_card")
	private String idCard;
	@Column(name = "org_id")
	private String orgId;
	@Column(name = "user_id")
	private String userId;

}
