package com.ido85.party.aaaa.mgmt.business.dto.notice;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class AddNoticeParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotNull
	private String noticeTitle;
	
	@NotNull
	private String noticeContent;
	
	private String noticeId;
	
}
