package com.ido85.party.sso.security.authentication.application.impl;

import com.ido85.party.sso.platform.data.datasource.TargetDataSource;
import com.ido85.party.sso.security.authentication.application.ExternalApplication;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.utils.ListUntils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
public class ExternalApplicationImpl implements ExternalApplication {
	@Inject
	private UserResources userRes;

	@Override
	@TargetDataSource(name="read")
	public List<User> queryUserInfoByUserId(List<String> hashs) {
		if(ListUntils.isNull(hashs)){
			return null;
		}
		return userRes.queryUserInfoByUserId(hashs);
	}

}
