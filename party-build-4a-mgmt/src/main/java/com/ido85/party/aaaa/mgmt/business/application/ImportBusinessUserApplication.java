package com.ido85.party.aaaa.mgmt.business.application;

import com.ido85.party.aaaa.mgmt.business.dto.CheckBusinessUserExistParam;

import javax.validation.Valid;
import java.util.List;

public interface ImportBusinessUserApplication {
	
	/**
	 * 在导入excel后，检测用户哪些已经存在，哪些不存在
	 */
	public List<String> checkBusinessUserExists(@Valid CheckBusinessUserExistParam param);
}
