package com.ido85.party.sso.filter;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.utils.StringUtils;

@Named
public class PreloadingAuthenticationUserDetailsService
		implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
	
	@Inject
	private TokenStore tokenStore;

	@Override
	public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
		Object o = token.getPrincipal();
		String principal = null;
		if( o instanceof String){
			principal = (String) token.getPrincipal();
		}else if(o instanceof UsernamePasswordAuthenticationToken){
			return (UserDetails)((UsernamePasswordAuthenticationToken)token.getPrincipal()).getPrincipal();
		}
		
		if (!StringUtils.isEmpty(principal)) {
			try{
				return (User)tokenStore.readAuthentication(principal).getPrincipal();
			}catch(Exception e){
				throw new UsernameNotFoundException("Token was not recognised");
			}
		}else{
			throw new UsernameNotFoundException("Token was not recognised");
		}
	}
}
