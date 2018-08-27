package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.security.utils.excel.ExcelField;

import lombok.Data;

@Data
public class UserInfo {
	@ExcelField(title = "身份证号", align = 1, sort = 20)
	private String idCard;
	@ExcelField(title = "姓名", align = 1, sort = 20)
	private String relName;
	@ExcelField(title = "手机号", align = 1, sort = 20)
	private Long telephone;

	private String isExist;
}
