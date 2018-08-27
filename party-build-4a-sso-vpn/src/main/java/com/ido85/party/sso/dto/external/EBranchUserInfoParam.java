package com.ido85.party.sso.dto.external;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
@Data
public class EBranchUserInfoParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> userIds;

}
