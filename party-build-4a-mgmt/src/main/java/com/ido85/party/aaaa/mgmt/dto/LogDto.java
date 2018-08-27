package com.ido85.party.aaaa.mgmt.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class LogDto implements Serializable{
	private static final long serialVersionUID = -3645922517429858023L;
	private String hasDetail;
	private String hostName;
	private String idCard;
	private Date logDate;
	private Long logId;
	private String logOperator;
	private String loginIP;
	private String loginName;
	private String mac;
	private String orgName;
	private String relName;
	private String result;
	private String url;

	public LogDto(String hasDetail, String hostName, String idCard, Date logDate,
				  Long logId, String logOperator, String loginIP, String loginName,
				  String mac, String orgName, String relName, String result, String url) {
		this.hasDetail = hasDetail;
		this.hostName = hostName;
		this.idCard = idCard;
		this.logDate = logDate;
		this.logId = logId;
		this.logOperator = logOperator;
		this.loginIP = loginIP;
		this.loginName = loginName;
		this.mac = mac;
		this.orgName = orgName;
		this.relName = relName;
		this.result = result;
		this.url = url;
	}
}
