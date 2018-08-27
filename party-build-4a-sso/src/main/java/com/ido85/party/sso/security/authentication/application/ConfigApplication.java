package com.ido85.party.sso.security.authentication.application;

import com.ido85.party.sso.security.authentication.domain.Config;

public interface ConfigApplication {
	/**
	 * 根据系统编码获取编码值
	 * @param configCode
	 * @return
	 */
	Config queryConfigInfoByCode(String configCode);

	int insertIntoConfig(String lastNum);

	int insertIntoUpdateConfig(String lastNum);

	int updateServerS();

	int updateServerOrig();

}
