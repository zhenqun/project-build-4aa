package com.ido85.party.sso.dto;

import lombok.Data;

@Data
public class SendMessageDto {
	
	private String returnstatus;
	
	private String message;
	
	private String remainpoint;
	
	private String taskID;
	
	private String successCounts;
}
