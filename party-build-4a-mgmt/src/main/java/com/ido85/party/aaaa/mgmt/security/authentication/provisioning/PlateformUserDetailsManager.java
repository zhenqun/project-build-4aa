/**
 * 
 */
package com.ido85.party.aaaa.mgmt.security.authentication.provisioning;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.exceptions.UserNotActivation;
import com.ido85.party.aaaa.mgmt.security.authentication.exceptions.UserNotEnabledException;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserResources;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.inject.Inject;
import javax.inject.Named;



/**
 * @author rongxj
 *
 */
@Named("userDetailsManager")
public class PlateformUserDetailsManager implements UserDetailsManager{

	@Inject
	private UserResources userResource;
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		User user = userResource.getUserByIdcardTelAccount(username);
		if (user == null) {
            throw new UsernameNotFoundException("用户  " + username + " 不存在");
        } else if(!user.isActivation()){
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
