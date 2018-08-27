package com.ido85.party.sso.security.authentication.common;

import com.ido85.party.sso.security.authentication.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {
	
	/**
	 * 获取当前登录用户
	 * @return
	 */
	public static User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = null;
		if(null != authentication){
			user = (User)authentication.getPrincipal();
		}
		return user;
	}
	
}
