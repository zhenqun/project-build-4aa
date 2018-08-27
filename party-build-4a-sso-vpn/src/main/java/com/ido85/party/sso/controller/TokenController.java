package com.ido85.party.sso.controller;

import javax.inject.Inject;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

	@Inject
	private TokenStore tokenStore;
	
//	/**
//	 * 撤销token
//	 */
//	@RequestMapping(path="/common/revokeToken",method=RequestMethod.GET)
//	public boolean revokeToken(){
//		OAuth2AccessToken token = tokenStore.readAccessToken("18678881276");
//		return true;
//	}
	
}
