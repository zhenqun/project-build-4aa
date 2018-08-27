package com.ido85.party.sso.security.authentication.application.impl;

import com.ido85.party.sso.dto.CenterUrlDto;
import com.ido85.party.sso.platform.data.datasource.TargetDataSource;
import com.ido85.party.sso.security.authentication.application.UserApplication;
import com.ido85.party.sso.security.authentication.domain.EmailLog;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.repository.CenterUrlResources;
import com.ido85.party.sso.security.authentication.repository.EmailLogResources;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.utils.CacheService;
import com.ido85.party.sso.security.utils.DateUtils;
import com.ido85.party.sso.security.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
public class UserApplicationImpl implements UserApplication {
	@Inject
	private UserResources userResource;
	@Inject
	private EmailLogResources emailLogRes;

	@Inject
	private CenterUrlResources centerUrlResources;

	@Inject
	private CacheService cacheService;

	@Override
	public User loadUserByUserEmail(String email) {
		return userResource.getUserByUserEmail(email);
	}
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

	public void  insertUser(User user) {
		 userResource.save(user);
		 
	}

	@Override
	public void updateLastLoginDate(String name) {
		userResource.updateLashLoginDate(name,new Date());
	}

	@Override
	public User getUserByLoginInfo(String username, String email, String telephone) {
		if(!StringUtils.isNull(username)){
			return  userResource.getUserByAccount(username);
		}
		if(!StringUtils.isNull(email)){
			return  userResource.getUserByUserEmail(email);
		}
		if(!StringUtils.isNull(telephone)){
			return  userResource.getUserByPhone(telephone);
		}
		return null;
	}
	@Override
	@TargetDataSource(name="read")
	public String getUserLogoByUsername(String username) {
		return userResource.getUserLogoByUsername(username);
	}
	@Override
	public User getUserByUserName(String username) {
		return userResource.getUserByAccount(username);
	}
	@Override
	public User getUserByLoginName(String telephone, String idCard) {
		String hash = StringUtils.getIDHash(idCard);
		User u = userResource.getUserbyAccountTelIdcard(telephone,hash);
		return u;
	}

	/**
	 * 根据登录名获取个人信息
	 * @param username
	 * @param idHash
     * @return
     */
	@TargetDataSource(name = "read")
	public User getUserbyAccountTelIdcard(String username, String idHash) {
		return userResource.getUserbyAccountTelIdcard(username,idHash);
	}

	/**
	 * 设置用户过期时间
	 * @param userId
	 */
	@TargetDataSource(name = "write")
	public void setUserExpireDate(String userId) {
		userResource.setUserExpireDate(userId, DateUtils.getDateAfterSecond(30*60));
	}

	/**
	 * 清除用户过期时间
	 */
	@TargetDataSource(name = "write")
	public void clearUserExpireDate(String id) {
		userResource.setUserExpireDate(id, null);
	}

	@Override
	@TargetDataSource(name = "read")
	public List<Object[]> getUserHash(String sendTime) {
		return userResource.getUserHash(sendTime);
	}
	@Override
	@TargetDataSource(name = "read")
	public List<Object[]> getUserUpdateTimeList(String updateTime) {
		return userResource.getUserUpdateTimeList(updateTime);
	}
	@Override
	@TargetDataSource(name = "read")
	public List<Object[]> updateTeleByAdminToSimple(String hash) {
		return userResource.updateTeleByAdminToSimple(hash);
	}

	/**
	 * 获取分中心默认登录地址
	 * @param area
	 * @return
	 */
	@Override
	@TargetDataSource(name = "read")
	public String getDefaultLoginUrl(String area) {
//		area = "liaocheng";
		CenterUrlDto out = null;
		//先从缓存中获取地址
		String cacheName = "centerurl";
		try {
			out = (CenterUrlDto) cacheService.get(cacheName, area);
			if(null != out){
				return out.getUrl();
			}
		} catch (Exception e) {
		}
		//如果缓存中获取不到地址,则从数据库中获取
		String url = centerUrlResources.getUrlBuArea(area);
		if(!StringUtils.isNull(url)){
			out = new CenterUrlDto();
			out.setArea(area);
			out.setUrl(url);
			cacheService.put(cacheName, area, out);
			return url;
		}else{
			return null;
		}
	}

	@Override
	@TargetDataSource(name = "read")
	public String getUserCreateTime(String hashValue) {
		return userResource.getUserCreateTime(hashValue);
	}
	@Override
	@TargetDataSource(name = "read")
	public String getUserUpdateTime(String hashValue) {
		return userResource.getUserUpdateTime(hashValue);
	}
	@Override
	public User getUserByTelephone(String telephone) {
		return userResource.getUserByPhone(telephone);
	}
	@TargetDataSource(name = "read")
	public int getUserId(Long id) {
		return userResource.getUserId(id);}

	@Override
	public User getUserByIdcardhash(String idCard,String name){
		String hash = StringUtils.getUserNameIDHash(name.trim(), idCard.trim());
		return userResource.getUserByIdcardHash(hash);
	}

}
