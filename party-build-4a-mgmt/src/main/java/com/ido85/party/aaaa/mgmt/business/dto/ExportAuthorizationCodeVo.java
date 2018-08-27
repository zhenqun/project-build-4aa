package com.ido85.party.aaaa.mgmt.business.dto;


import com.ido85.party.aaaa.mgmt.security.utils.excel.ExcelField;

import lombok.Data;

@Data
public class ExportAuthorizationCodeVo {
	
	@ExcelField(title = "身份证号", align = 1, sort = 20)
	private String idCard;
	
	@ExcelField(title = "姓名", align = 1, sort = 20)
	private String relName;
	
	@ExcelField(title = "授权码", align = 1, sort = 20)
	private String authorizationCode;
	
}