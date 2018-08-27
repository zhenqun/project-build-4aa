/**
 * 
 */
package com.ido85.party.sso.security.authentication.provisioning;

import com.ido85.party.sso.security.authentication.application.UserApplication;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.exceptions.UserNotActivation;
import com.ido85.party.sso.security.authentication.exceptions.UserNotEnabledException;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * userdetail管理
 * @author rongxj
 *
 */
@Named("userDetailsManager")
public class PlateformUserDetailsManager implements UserDetailsManager{

	@Inject
	private UserResources userResource;
	
	@Inject
	private UserApplication userApp;
	
	/**
	 * 根据用户名加载userDetail,供 authenticationProvider使用
	 */
	@Override
	public UserDetails loadUserByUsername(String username)// 重写了 UserDetailsService 中的 loadUserByUsername 方法
			throws UsernameNotFoundException {
//		UserDetails user = userResource.getUserByUserEmail(username);
		User user = userResource.getUserByAccount(username);
		if (user == null) {
            throw new UsernameNotFoundException("用户  " + username + " 不存在");
        } else if (!user.isActivation()){
			throw new UserNotActivation("用户  " + username + "  未激活");
		}else if (user.isAccountLocked()) {
            throw new UserNotEnabledException("用户  " + username + " 被锁定");
        } else if (user.isAccountExpired()) {
        	throw new UserNotEnabledException("用户  " + username + " 已注销");
        }
		return user;
	}

	@Override
	public void createUser(UserDetails user) {
	}

	@Override
	public void updateUser(UserDetails user) {
	}

	@Override
	public void deleteUser(String username) {
		
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		
	}

	@Override
	public boolean userExists(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
