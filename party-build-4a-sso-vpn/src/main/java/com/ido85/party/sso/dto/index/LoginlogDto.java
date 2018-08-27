package com.ido85.party.sso.dto.index;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
@Data
public class LoginlogDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String logId;
	
	private String clientName;
	
	private Date loginDate;
	
	private String dateDetail;
	
	private Integer pageNo;
	
	private Integer pageSize;

}
