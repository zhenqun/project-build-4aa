/**
 * 
 */
package com.ido85.party.sso.security.authentication.provisioning;

import com.ido85.party.sso.platform.data.datasource.TargetDataSource;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.exceptions.UserExpireException;
import com.ido85.party.sso.security.authentication.exceptions.UserNotEnabledException;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.utils.DateUtils;
import com.ido85.party.sso.security.utils.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;


/**
 * @author rongxj
 *
 */
@Named("userDetailsManager")
public class PlateformUserDetailsManager implements UserDetailsManager{

	@Inject
	private UserResources userResource;
	
	@Override
	@TargetDataSource(name = "read")
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
//		User user = userResource.getUserByAccount(username);
		//用户名  手机号和身份证号都可以登录，如果是身份证号的话，需要hash之后做对比
		String idCardHash = StringUtils.getIDHash(username);
		User user = userResource.getUserbyAccountTelIdcard(username,idCardHash);
		if (user == null) {
            throw new UsernameNotFoundException("用户  " + username + " 不存在");
        } else if (user.isAccountLocked()) {
            throw new UserNotEnabledException("用户  " + username + " 被锁定");
        } else if (user.isAccountExpired()) {
        	throw new UserNotEnabledException("用户  " + username + " 已注销");
        } else if(null != user.getExpireDate() && DateUtils.difference(new Date(),user.getExpireDate()) < 0){
			throw new UserExpireException("用户  " + username + " 已锁定,请于"+DateUtils.formatDate(user.getExpireDate(),"HH:mm:ss")+"后重新尝试!");
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
