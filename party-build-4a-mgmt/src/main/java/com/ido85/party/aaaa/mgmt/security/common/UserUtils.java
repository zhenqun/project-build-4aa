package com.ido85.party.aaaa.mgmt.security.common;

import javax.inject.Inject;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserClientRel;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserClientRelResource;

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
