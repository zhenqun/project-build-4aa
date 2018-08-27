package com.ido85.party.aaaa.mgmt.security.authentication.application;

import com.ido85.party.aaaa.mgmt.dto.AdminDto;
import com.ido85.party.aaaa.mgmt.dto.AdminQueryParam;
import com.ido85.party.aaaa.mgmt.dto.ApplicationAllotParam;
import com.ido85.party.aaaa.mgmt.dto.userinfo.ModifyPasswordParam;
import com.ido85.party.aaaa.mgmt.internet.dto.ApplicationDto;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.SecurityLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/***
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
	 * 修改密码
	 * @param id
	 * @param password
	 * @return
	 */
	public int changePassword(Long id, String password);
	/**
	 * 添加用户
	 * @param user
	 * @return
	 */
	public void  insertUser(User user);
	
	/**
	 * 根据查询条件查询管理员
	 * @param param
	 * @return
	 */
	public List<AdminDto> getAdminByCondition(AdminQueryParam param);
	
	/**
	 * 修改管理员状态
	 * @param status
	 * @return
	 */
	public boolean modifyAdminStatus(Long userId,Integer status);
	
	/**
	 * 系统分配
	 * @param param
	 * @return
	 */
	public boolean applicationAllot(ApplicationAllotParam param);
	
	public Long getAdminCntByCondition(AdminQueryParam param);
	
	/**
	 * 更新最后登录时间
	 * @param name
	 */
	public void updateLastLoginDate(String name);
	
	/**
	 * 根据管理员id查询管理员
	 * @param userId
	 * @return
	 */
	public User getAdminById(Long userId);
	
	/**
	 * 保存admin
	 * @param user
	 */
	public void saveAdmin(User user);
	
	/**
	 * 查询admin应用
	 * @return
	 */
	public List<ApplicationDto> applicationQuery();

	/**
	 * 根据哈希值获取用户信息
	 * @param hash
	 * @return
	 */
	User getUserByHash(String hash);
	
	/**
	 * 获取用户
	 * @param username
	 * @return
	 */
	User getUserByUserName(String username);
	
	/**
	 * 根据真实姓名和身份证号查询用户
	 * @param name
	 * @param idCard
	 * @return
	 */
	public User getUserByIdcardAndName(String idCard);
	
	/**
	 * 根据ids获取姓名map
	 * @param userIds
	 * @return
	 */
	public Map<String, String> getUserByIds(Set<String> userIds);
	
	/**
	 * 检查用户密码是否过期
	 * @param username
	 * @return
	 */
	public Long checkUserPwd(String username);
	
	/**
	 * 获取当前登录用户的ou
	 * @return
	 */
	public String getCurrentUserOu();

	/**
	 * 修改密码
	 * @param param
	 * @return
     */
	Map<String,Object> modifyPassword(ModifyPasswordParam param);


	int updateMobile(String id ,String newMobile);

	Map<String, Object> checkVerifyCode(String newMobile,String verifyCode);


    List<User> checkHashAdmin(List<String> hashs);
}
