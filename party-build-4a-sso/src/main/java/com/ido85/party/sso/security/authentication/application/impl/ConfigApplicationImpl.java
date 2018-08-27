package com.ido85.party.sso.security.authentication.application.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.ido85.party.sso.platform.data.datasource.TargetDataSource;
import com.ido85.party.sso.security.authentication.application.ConfigApplication;
import com.ido85.party.sso.security.authentication.domain.Config;
import com.ido85.party.sso.security.authentication.repository.ConfigRepository;


@Named
public class ConfigApplicationImpl implements ConfigApplication {
	@Inject
	private ConfigRepository configRep;

	@Override
	@TargetDataSource(name = "read")
	public Config queryConfigInfoByCode(String configCode) {
		return configRep.queryConfigInfoByCode(configCode);
	}

	@Override
	@TargetDataSource(name = "write")
	public int insertIntoConfig(String lastNum) {
		return configRep.insertIntoConfig(lastNum);
	}
	@Override
	@TargetDataSource(name = "write")
	public int insertIntoUpdateConfig(String lastNum) {
		return configRep.insertIntoUpdateConfig(lastNum);
	}

	@Override
	@TargetDataSource(name = "write")
	public int updateServerS() {
		return configRep.updateS();
	}

	@TargetDataSource(name = "write")
	public  int updateServerOrig(){
		return configRep.updateOrig();
	}

}
