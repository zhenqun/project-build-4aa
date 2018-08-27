package com.ido85.party.aaaa.mgmt.internet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by fire on 2017/3/1.
 */
@Data
@NoArgsConstructor
public class OutRegisterLogQueryDto {
	private String hostName;
	private String idCard;
	private Date logDate;
	private String logOperator;
	private String registerIP;
	private String registerName;
	private String mac;
	private String orgName;
	private String relName;
	private String result;

	public OutRegisterLogQueryDto(String hostName, String idCard, Date logDate,
								  String logOperator, String registerIP, String registerName,
								  String mac, String orgName, String relName, String result) {
		this.hostName = hostName;
		this.idCard = idCard;
		this.logDate = logDate;
		this.logOperator = logOperator;
		this.registerIP = registerIP;
		this.registerName = registerName;
		this.mac = mac;
		this.orgName = orgName;
		this.relName = relName;
		this.result = result;
	}
}
