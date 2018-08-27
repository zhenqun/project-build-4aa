package com.ido85.party.sso.security.authentication.application.impl;

import com.ido85.party.sso.security.authentication.application.MessageLogApplication;
import com.ido85.party.sso.security.authentication.domain.MessageLog;
import com.ido85.party.sso.security.utils.ListUntils;
import com.ido85.party.sso.security.utils.StringUtils;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
@Named
public class MessageLogApplicationImpl implements MessageLogApplication {

	@PersistenceContext(unitName = "system")
	private EntityManager em;

	@Override
	public MessageLog getLastMessageByMobile(String mobile) {
		StringBuffer sb = new StringBuffer("select m from MessageLog m");
		if(StringUtils.isNull(mobile)){
			return null;
		}
		sb.append(" where m.telephone = :telephone order by m.createDate desc");
		Query q = em.createQuery(sb.toString());
		q.setParameter("telephone", mobile);
		q.setFirstResult(0);
		q.setMaxResults(1);
		List<Object> oList = q.getResultList();
		MessageLog messageLog = null;
		if(ListUntils.isNotNull(oList)){
			messageLog = (MessageLog)oList.get(0);
		}else{
			return null;
		}
		return messageLog;
	}
	
	
}
