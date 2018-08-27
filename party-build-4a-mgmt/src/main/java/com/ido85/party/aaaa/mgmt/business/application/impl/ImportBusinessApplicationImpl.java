package com.ido85.party.aaaa.mgmt.business.application.impl;

import com.ido85.party.aaaa.mgmt.business.application.ImportBusinessUserApplication;
import com.ido85.party.aaaa.mgmt.business.domain.BusinessUser;
import com.ido85.party.aaaa.mgmt.business.dto.CheckBusinessUserExistParam;
import com.ido85.party.aaaa.mgmt.business.dto.CheckBusinessUserParam;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
@Named
public class ImportBusinessApplicationImpl implements ImportBusinessUserApplication{

	@PersistenceContext(unitName = "business")
	private EntityManager businessEntity;

	@Override
	/**
	 * 查询这些用户是否存在
	 */
	public List<String> checkBusinessUserExists(@Valid CheckBusinessUserExistParam param) {
		List<String> result = null;
		String idCard = null;
		String name = null;
		List<String> hashs = null;
		List<BusinessUser> userList = null;
		String clientId = param.getClientId();
		if(ListUntils.isNotNull(param.getUsers())){
			userList = new ArrayList<>();
			result = new ArrayList<String>();
			hashs = new ArrayList<String>();
			for(CheckBusinessUserParam p:param.getUsers()){
				idCard = p.getIdCard();
				name = p.getName();
				hashs.add(StringUtils.getUserNameIDHash(name, idCard));
			}
			userList = getUserListByHashsAndClientId(hashs,clientId);
			if(ListUntils.isNotNull(userList)){
				for(BusinessUser u:userList){
					result.add(u.getIdCard()+"|"+u.getName());
				}
			}
		}
		return result;
	}

	private List<BusinessUser> getUserListByHashsAndClientId(List<String> hashs, String clientId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select u from BusinessUser u ,ClientUserRel l where u.id = l.userId and l.clientId = :clientId");
		if(ListUntils.isNotNull(hashs)){
			sb.append(" and u.hash in :hashs");
		}
		Query q = businessEntity.createQuery(sb.toString(),BusinessUser.class);
		if(ListUntils.isNotNull(hashs)){
			q.setParameter("hashs",hashs);
		}
		q.setParameter("clientId",clientId);
		List<BusinessUser> userList = q.getResultList();
		return userList;
	}

}
