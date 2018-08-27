package com.ido85.party.sso.dto.index;

import java.io.Serializable;

import lombok.Data;
@Data
public class NoticeQueryParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer pageNo;
	
	private Integer pageSize;

}
