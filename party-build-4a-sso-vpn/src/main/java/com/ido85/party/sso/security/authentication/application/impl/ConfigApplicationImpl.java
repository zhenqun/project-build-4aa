package com.ido85.party.sso.security.authentication.application.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.ido85.party.sso.security.authentication.application.ConfigApplication;
import com.ido85.party.sso.security.authentication.domain.Config;
import com.ido85.party.sso.security.authentication.repository.ConfigRepository;


@Named
public class ConfigApplicationImpl implements ConfigApplication {
	@Inject
	private ConfigRepository configRep;

	@Override
	public Config queryConfigInfoByCode(String configCode) {
		return configRep.queryConfigInfoByCode(configCode);
	}

}
