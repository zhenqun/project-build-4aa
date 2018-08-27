package com.ido85.party.aaa.authentication.application;

import com.ido85.party.aaa.authentication.dto.UserAyscDto;
import com.ido85.party.aaa.authentication.entity.User;

import java.util.List;

/***
 * 网上 会员用户
 * @author shishuai
 *
 */
public interface UserApplication {
	/**
	 * 根据信息查询用户
	 * @param username
	 * @param email
	 * @return
	 */
	public User getUserByLoginInfo(String username, String email, String telephone);
	
	/**
	 * 根据用户名查询头像
	 * @param username
	 * @return
	 */
	public String getUserLogoByUsername(String username);
	
	/**
	 * 根据用户名获取用户
	 * @param username
	 * @return
	 */
	public User getUserByUserName(String username);

	public void insertUser(User newUser);

	public User getUserByLoginName(String telephone, String idCard);

	/**
	 * 根据认证信息userid获取账号
	 * @param userIds
	 * @return
	 */
    List<UserAyscDto> getUserByAuthUserIds(List<String> userIds, String roleId);
}
