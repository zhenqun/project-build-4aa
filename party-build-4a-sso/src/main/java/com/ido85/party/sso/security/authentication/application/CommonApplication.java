package com.ido85.party.sso.security.authentication.application;

import com.ido85.party.sso.security.oauth2.domain.PlatformClientDetails;

import java.util.Map;

public interface CommonApplication {

	/**
	 * 校验手机验证码
	 * @param telephone
	 * @param register
	 * @return
	 */
	Map<String, Object> isVerifyCodeValid(String telephone, String type,String veifyCode);

	PlatformClientDetails getClientByClientId(String clientId);
}
