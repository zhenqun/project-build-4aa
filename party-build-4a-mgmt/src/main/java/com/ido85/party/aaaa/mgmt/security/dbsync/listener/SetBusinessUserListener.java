package com.ido85.party.aaaa.mgmt.security.dbsync.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;

import com.ido85.party.aaaa.mgmt.business.domain.BusinessUser;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.GrantLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.LogDetail;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.SetAuditorManageScopeParam;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.GrantLogResources;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.LogDetailResources;
import com.ido85.party.aaaa.mgmt.security.constants.LogConstants;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.AddAuditorEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.GrantAuditorEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.SetAuditorEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.SetBusinessUserEvent;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;


/**
 * @ClassName: GrantAuditorListener
 * @Description: TODO
 * @author: yin
 */
@Named
public class SetBusinessUserListener implements ApplicationListener<SetBusinessUserEvent> {

	@Inject
	private GrantLogResources grantLogResources;
	
	@Inject
	private LogDetailResources logDetailResources;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Override
	@Transactional
	@Async
	public void onApplicationEvent(SetBusinessUserEvent event) {
		List<LogDetail> detailList = null;
		List<BusinessUser> userList = event.getOperData();
		LogDetail logDetail = null;
		GrantLog grantLog = event.getOperLog();
		Long logId = null;
		SetAuditorManageScopeParam param = (SetAuditorManageScopeParam) event.getOldOperData();
		if(null != grantLog){
			grantLogResources.save(grantLog);
			logId = grantLog.getGrantLogId();
		}
		if(ListUntils.isNotNull(userList)){
			for(BusinessUser u:userList){
				detailList = new ArrayList<LogDetail>();
				if(null != u){
					logDetail = new LogDetail();
					logDetail.setColumnName("manage_org_name");
					logDetail.setCurrentValue(param.getManageOrgName());
					logDetail.setLogDetailId(idGenerator.next());
					logDetail.setLogId(logId);
//					logDetail.setOriginValue(u.getManageOrgName());
					logDetail.setRemarks("管理范围");
					logDetail.setTableName("t_4a_actors");
//					logDetail.setGrantOrgId(u.getOrgId());
//					logDetail.setGrantOrgName(u.getOrgName());
					logDetail.setGrantRelName(u.getName());
					logDetail.setGrantUserId(u.getId());
					detailList.add(logDetail);
				}
			}
		}
		if(ListUntils.isNotNull(detailList)){
			logDetailResources.save(detailList);
		}
	}
}
