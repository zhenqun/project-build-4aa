package com.ido85.party.sso.security.authentication.application;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ido85.party.sso.dto.RoleDto;
import com.ido85.party.sso.security.authentication.domain.EmailLog;
import com.ido85.party.sso.security.authentication.domain.User;

/***
 * 网上 会员用户
 * @author shishuai
 *
 */
public interface UserApplication {
//	/***
//	 * 根据邮箱  查找用户信息
//	 * @param Email
//	 * @return
//	 */
//	public User loadUserByUserEmail(String Email);
	/***
	 * 插入日志
	 * @param log
	 */
	public void insertEamilLog( EmailLog log);
	/***
	 * 查询日志
	 * @param logId
	 */
	public EmailLog getEmailLogByLogId(Long logId);
	/***
	 * 修改密码
	 * @param id
	 * @param password
	 * @return
	 */
	public int changePassword(String id, String password);
	/**
	 * 添加用户
	 * @param user
	 * @return
	 */
	public void  insertUser(User user);
	
	/***
	 * 实名认证
	 * @param in
	 * @return
	 */
	public boolean certification( Map<String, Object> in);
	

	/**
	 * 更新最后登录时间
	 * @param name
	 */
	public void updateLastLoginDate(String name);
	
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
	
	/**
	 * 根据真实姓名和身份证号查询用户
	 * @param name
	 * @param idCard
	 * @return
	 */
	public User getUserByIdcardAndName(String name, String idCard);
	
	/**
	 * 获取用户的角色以及权限
	 * @param userId
	 * @return
	 */
	public List<RoleDto> getUserRolePermission(String userId);


	Map<String,Object> checkVerifyCode(String mobile,String verifyCode);

	/**
	 * 更新手机号
	 * @param userId
	 * @param mobile
	 * @return
	 */
	int updateMobile(String userId,String mobile);
	
	
}
