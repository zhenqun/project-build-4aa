package com.ido85.party.sm.application.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.ido85.party.platform.utils.ListUntils;
import com.ido85.party.platform.utils.StringUtils;
import com.ido85.party.sm.application.MessageLogApplication;
import com.ido85.party.sm.domain.MessageLog;
import com.ido85.party.sm.resource.MessageLogResources;

@Named
public class MessageLogApplicationImpl implements MessageLogApplication {

	@Inject
	private EntityManager em;

	@Inject
	private MessageLogResources messageLogResources;

	@Override
	public MessageLog getLastMessageByMobile(String mobile) {
		StringBuffer sb = new StringBuffer("select m from MessageLog m");
		if(StringUtils.isNull(mobile)){
			return null;
		}
		sb.append(" where m.telephone = :telephone and m.isSuccess = 't'  order by createDate desc");
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

	@Override
	public Long getTelSendCount(String telephone, Date startDate, Date endDate) {
		Long cnt = messageLogResources.getTelSendCount(telephone,startDate,endDate);
		return cnt;
	}


}
