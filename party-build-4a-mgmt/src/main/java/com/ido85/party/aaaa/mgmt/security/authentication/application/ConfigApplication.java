package com.ido85.party.aaaa.mgmt.security.authentication.application;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.Config;

public interface ConfigApplication {
	/**
	 * 根据系统编码获取编码值
	 * @param configCode
	 * @return
	 */
	Config queryConfigInfoByCode(String configCode);
}
