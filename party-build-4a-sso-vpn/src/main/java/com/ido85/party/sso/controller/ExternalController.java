package com.ido85.party.sso.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.ido85.party.sso.dto.external.*;
import com.ido85.party.sso.security.authentication.application.ExternalApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.utils.ListUntils;
import com.ido85.party.sso.security.utils.StringReplaceUtils;
import com.ido85.party.sso.security.utils.StringUtils;
/**
 * 对外接口
 * @author yin
 *
 */
@RestController
public class ExternalController {
	
	@Inject
	private UserResources userResourcs;
	
	@Value("${userLogoUrl}")
	private String userLogoUrl;

	@Inject
	private ExternalApplication externalApplication;

	/**
	 * 根据userid返回用户部分信息
	 * @return
	 */
	@RequestMapping(path="/common/getUserInfoByIds", method={RequestMethod.POST})
	@ResponseBody
	public List<EBranchUserInfoDto> getEBranchUserInfoByIds(@RequestBody EBranchUserInfoParam param){
		List<String> userIds = param.getUserIds();
		List<User> list = null;
		List<EBranchUserInfoDto> dtoList = null;
		EBranchUserInfoDto dto = null;
		if(ListUntils.isNotNull(userIds)){
			list = userResourcs.getEBranchUserInfoByIds(userIds);
			if(ListUntils.isNotNull(list)){
				dtoList = new ArrayList<EBranchUserInfoDto>();
				for(User u:list){
					dto = new EBranchUserInfoDto();
					if(!StringUtils.isNull(u.getLogo())){
						dto.setLogo(userLogoUrl+u.getLogo());
					}
					dto.setName(StringReplaceUtils.getStarString(u.getName()));
					dto.setUserId(u.getId());
					dtoList.add(dto);
				}
			}
		}
		return dtoList;
	}

	@Inject
	private EntityManager em;

	/**
	 * 检测一个组织下面是否有组织关系转接的管理员
	 * @return
	 */
	@RequestMapping(path="/common/checkHaveAdmin", method={RequestMethod.POST})
	@ResponseBody
	public boolean getEBranchUserInfoByIds(@RequestBody CheckHaveAdmin param){
		String clientId = param.getClientId();
		String orgId = param.getOrgId();
		String permissionName = param.getPermissionName();
		StringBuffer sb = new StringBuffer("SELECT\n" +
				"	count(t.id)\n" +
				"FROM\n" +
				"   t_4a_actors t,\n" +
				"	r_4a_user_role u,\n" +
				"	r_4a_role_permission rp,\n" +
				"	t_4a_permission P\n" +
				"WHERE\n" +
				"   t.id = u.user_id and \n" +
				"	u.role_id = rp.role_id\n" +
				"AND rp.permission_id = P .permission_id\n" +
				"AND P .client_id = :clientId\n" +
				"AND P .permission_name = :permissionName\n" +
				"AND u.manage_id = :orgId");
		Query q = em.createNativeQuery(sb.toString());
		q.setParameter("clientId",clientId);
		q.setParameter("permissionName",permissionName);
		q.setParameter("orgId",orgId);
		Long cnt = StringUtils.toLong(q.getSingleResult());
		if(cnt.longValue() > 0L){
			return true;
		}
		return false;
	}

	/**
	 * 根据userid返回用户部分信息
	 * @return
	 */
	@RequestMapping(path="/common/getFirstSecretaryClass", method={RequestMethod.POST})
	@ResponseBody
	public List<FirstSecretaryClssDto> getEBranchUserInfoByIds(@RequestBody FirstSecretaryClssParam param){
		List<FirstSecretaryClssDto> firstSecretaryClssDtos = externalApplication.getEBranchUserInfoByIds(param);
		return firstSecretaryClssDtos;
	}
}
