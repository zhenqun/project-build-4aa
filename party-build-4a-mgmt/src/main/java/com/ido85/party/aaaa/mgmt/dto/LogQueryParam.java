package com.ido85.party.aaaa.mgmt.dto;

import com.ido85.party.aaaa.mgmt.dto.base.InBasePageDto;
import lombok.Data;

@Data
public class LogQueryParam extends InBasePageDto {
	
	private String endDate;
	
	private String logOperator;
	
	private String logType;
	
	private String startDate;

}
