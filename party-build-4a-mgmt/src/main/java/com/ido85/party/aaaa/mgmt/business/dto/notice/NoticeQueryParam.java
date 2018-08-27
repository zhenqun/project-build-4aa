package com.ido85.party.aaaa.mgmt.business.dto.notice;

import java.io.Serializable;

import lombok.Data;
@Data
public class NoticeQueryParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String noticeTitle;
	
	private Integer pageNo;
	
	private Integer pageSize;

}
