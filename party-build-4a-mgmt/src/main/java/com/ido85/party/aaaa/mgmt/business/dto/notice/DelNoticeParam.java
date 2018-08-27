package com.ido85.party.aaaa.mgmt.business.dto.notice;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class DelNoticeParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private List<String> ids;

}
