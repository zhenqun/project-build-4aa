package com.ido85.party.aaaa.mgmt.security.authentication.application.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.security.authentication.application.GrantLogApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.GrantLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.constants.LogConstants;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.GrantAuditorEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.OperLogEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.factory.EventFactory;
import com.ido85.party.aaaa.mgmt.security.dbsync.spring.InstanceFactory;
import com.ido85.party.aaaa.mgmt.security.utils.IPHostUtils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
@Named
public class GrantLogApplicationImpl implements GrantLogApplication {

	@Inject
	private IdGenerator idGenerator;
	
	/**
	 * 增加日志
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "hiding" })
	public <T> void addLog(User user,GrantLog grantLog, String addAuditor, String addAuditorName, String hasDetail, String message,T detailList,T oldDetailList) throws Exception {
		//详细日志
		Long logId = idGenerator.next();
		grantLog.setBusinessName(addAuditorName);
		grantLog.setCreateDate(new Date());
		grantLog.setGrantLogId(logId);
		grantLog.setHasDetail(hasDetail);
		grantLog.setOperateBy(user.getId());
		grantLog.setOperateName(user.getUsername());
//		grantLog.setOperateOrgId(user.getOrgId());
//		grantLog.setOperateOrgName(user.getOrgName());
		grantLog.setOperateRelName(user.getName());
		grantLog.setOperateType(addAuditor);
		grantLog.setRemarks(message);
//		grantLog.setOrgCode(user.getOrgCode());
		if(LogConstants.EXPORT_AUDITOR_CODE.equals(addAuditor)){
			grantLog.setUrl(StringUtils.toString(oldDetailList));
		}
		OperLogEvent event = EventFactory.createEvent(addAuditor);
		event.setOperLog(grantLog);
		event.setOperData(detailList);
		event.setOldOperData(oldDetailList);
		InstanceFactory.publishEvent(event);
	}
	
}
