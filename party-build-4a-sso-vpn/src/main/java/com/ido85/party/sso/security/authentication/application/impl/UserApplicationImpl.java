package com.ido85.party.sso.security.authentication.application.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import com.ido85.party.sso.dto.RoleDto;
import com.ido85.party.sso.security.authentication.application.CommonApplication;
import com.ido85.party.sso.security.authentication.application.UserApplication;
import com.ido85.party.sso.security.authentication.domain.EmailLog;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.repository.EmailLogResources;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.authentication.repository.UserRoleResource;
import com.ido85.party.sso.security.constants.SendMessageConstants;
import com.ido85.party.sso.security.utils.StringUtils;

@Named
public class UserApplicationImpl implements UserApplication {
	@Inject
	private UserResources userResource;
	@Inject
	private EmailLogResources emailLogRes;
	@Inject
	private UserRoleResource userRoleResource;

	@Inject
	private CommonApplication commonApplication;
	
//	@Override
//	public User loadUserByUserEmail(String email) {
////		return userResource.getUserByUserEmail(email);
//	}

	@Transactional
	@Override
	public void insertEamilLog(EmailLog log) {
		emailLogRes.save(log);
	}

	@Override
	public EmailLog getEmailLogByLogId(Long logId) {
		return emailLogRes.getEmailLogInfo(logId);
	}

	@Override
	public int changePassword(String id, String password) {
		return userResource.changePassword(password, id);
	}

	@Override
	public void insertUser(User user) {
		userResource.save(user);

	}

	@Override
	public boolean certification(Map<String, Object> in) {
		return true;
	}

	@Override
	public void updateLastLoginDate(String name) {
		userResource.updateLashLoginDate(name, new Date());
	}

	@Override
	public User getUserByLoginInfo(String username, String email, String telephone) {
		if (!StringUtils.isNull(username)) {
			return userResource.getUserByAccount(username);
		}
//		if (!StringUtils.isNull(email)) {
//			return userResource.getUserByUserEmail(email);
//		}
		if (!StringUtils.isNull(telephone)) {
			return userResource.getUserByPhone(telephone);
		}
		return null;
	}

	@Override
	public String getUserLogoByUsername(String username) {
		return userResource.getUserLogoByUsername(username);
	}

	@Override
	public User getUserByUserName(String username) {
		return userResource.getUserByAccount(username);
	}

	@Override
	public User getUserByIdcardAndName(String name, String idCard) {
		return userResource.getUserByIdcardAndName(name, idCard);
	}

	/**
	 * 获取用户角色权限
	 */
	public List<RoleDto> getUserRolePermission(String userId) {
//		List<UserRole>userRoleResource.getRoleByUserId(userId);
		return null;
	}

	@Override
	public Map<String,Object> checkVerifyCode(String mobile, String verifyCode) {
		Map<String,Object> map = commonApplication.isVerifyCodeValid(mobile, SendMessageConstants.NEWPHONEVERIFY,verifyCode);
		if(map.containsKey("flag") && map.get("flag").equals("success")){
			return map;
		}else{
			return  map;
		}
	}

	@Override
	public int updateMobile(String userId, String mobile) {
		return userResource.updateMobile(userId,mobile);
	}


}
