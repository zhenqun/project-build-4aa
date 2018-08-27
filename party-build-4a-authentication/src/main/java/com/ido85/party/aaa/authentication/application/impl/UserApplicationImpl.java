package com.ido85.party.aaa.authentication.application.impl;


import com.ido85.party.aaa.authentication.application.UserApplication;
import com.ido85.party.aaa.authentication.dto.UserAyscDto;
import com.ido85.party.aaa.authentication.entity.User;
import com.ido85.party.aaa.authentication.reposities.UserResources;
import com.ido85.party.aaa.authentication.reposities.UserRoleResource;
import com.ido85.party.platform.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Named
public class UserApplicationImpl implements UserApplication {
	@Inject
	private UserResources userResource;

	@Inject
	private UserRoleResource userRoleResource;

	@Override
	public User getUserByLoginInfo(String username, String email, String telephone) {
//		if(!StringUtils.isNull(username)){
//			return  userResource.getUserByAccount(username);
//		}
//		if(!StringUtils.isNull(email)){
//			return  userResource.getUserByUserEmail(email);
//		}
//		if(!StringUtils.isNull(telephone)){
//			return  userResource.getUserByPhone(telephone);
//		}
		return null;
	}
	@Override
	public String getUserLogoByUsername(String username) {
//		return userResource.getUserLogoByUsername(username);
		return null;
	}
	@Override
	public User getUserByUserName(String username) {
//		return userResource.getUserByAccount(username);
		return null;
	}
	@Override
	public void  insertUser(User user) {
		 userResource.save(user);
		 
	}
	@Override
	public User getUserByLoginName(String telephone, String idCard) {
//		String hash = StringUtils.getIDHash(idCard);
//		User u = userResource.getUserByPhoneIdcard(telephone,idCard);
//		if(u == null){
//			u = userResource.getUserByIDHash(hash);
//		}
//		return u;
		return null;
	}

	@Inject
	private EntityManager em;

	@Override
	//String authUserId, String userId, String name, String hash, String idCardHash
	public List<UserAyscDto> getUserByAuthUserIds(List<String> userIds, String roleId) {
		Long rId = StringUtils.toLong(roleId);
//		List<Long> uIds = userRoleResource.getUserIdsByauthUserid(userIds,roleId);
//		List<User> userList = userResource.getUsersByIds(uIds);
		StringBuffer sb = new StringBuffer("select new com.ido85.party.aaa.authentication.dto.UserAyscDto(i.userId,t) " +
				" from User t,AuthenticationInfo i,UserRole r " +
				" where t.id = r.userId and r.authenticationUserId = i.userId and r.authenticationUserId in :userIds and i.roleId = :roleId");
		Query q = em.createQuery(sb.toString());
		q.setParameter("userIds",userIds);
		q.setParameter("roleId",StringUtils.toLong( roleId));
		List<UserAyscDto> dtoList = q.getResultList();
		return dtoList;
	}
}
