package com.ido85.party.aaaa.mgmt.business.dto.notice;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
@Data
public class NoticeDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String noticeId;
	
	private String noticeTitle;
	
	private String noticeContent;
	
	private Date createDate;
	
	private String createBy;
	
	private Date updateDate;
	
	private String updateBy;
	
	private boolean isRelease;
}
