package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by fire on 2017/2/28.
 */
@Data
@NoArgsConstructor
public class OutGrantLogDetailDto {
	private String currentValue;
	private String orgName;
	private String originValue;
	private String relName;
	private String remarks;

	public OutGrantLogDetailDto(String currentValue, String orgName, String originValue, String relName, String remarks) {
		this.currentValue = currentValue;
		this.orgName = orgName;
		this.originValue = originValue;
		this.relName = relName;
		this.remarks = remarks;
	}
}
