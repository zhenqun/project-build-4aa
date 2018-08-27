package com.ido85.party.sso.security.authentication.application;

import com.ido85.party.sso.security.authentication.domain.EmailLog;
import com.ido85.party.sso.security.authentication.domain.User;

import java.util.Date;
import java.util.List;

/***
 * 网上 会员用户
 * @author shishuai
 *
 */
public interface UserApplication {
	/***
	 * 根据邮箱  查找用户信息
	 * @param Email
	 * @return
	 */
	public User loadUserByUserEmail(String Email);
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
	public User getUserByLoginName(String telephone, String idCard);

	/**
	 * 根据登录名获取个人信息
	 * @param username
	 * @param idHash
     * @return
     */
	User getUserbyAccountTelIdcard(String username, String idHash);

	/**
	 * 设置用户锁定时间
	 * @param userId
	 */
    void setUserExpireDate(String userId);

	void clearUserExpireDate(String id);

	/**
	 * 查询用户的hash值
	 * @return
	 */
	List<Object[]> getUserHash(String sendTime);

	/**
	 * 通过hash获取创建时间
	 * @param hashValue
	 * @return
	 */
	String getUserCreateTime(String hashValue);

	String getUserUpdateTime(String hashValue);

	User getUserByTelephone(String telephone);
	int getUserId(Long id);

	User getUserByIdcardhash(String idCard,String name);

	List<Object[]> getUserUpdateTimeList(String updateTime);

	List<Object[]> updateTeleByAdminToSimple(String hash);
	/**
	 * 获取分中心默认登录地址
	 * @param area
	 * @return
	 */
    String getDefaultLoginUrl(String area);
}
