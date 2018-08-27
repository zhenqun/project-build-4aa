package com.ido85.party.aaaa.mgmt.security.dbsync.listener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.context.ApplicationListener;

import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.GrantLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.LogDetail;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.ModifyAuditorStatusParam;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.GrantLogResources;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.LogDetailResources;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.ModAuditorStateEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.ModSecurityUserStateEvent;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;


/**
 * @ClassName: GrantAuditorListener
 * @Description: TODO
 * @author: yin
 */
@Named
public class ModSecurityUserStateListener implements ApplicationListener<ModSecurityUserStateEvent> {

	@Inject
	private GrantLogResources grantLogResources;
	
	@Inject
	private LogDetailResources logDetailResources;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Override
	public void onApplicationEvent(ModSecurityUserStateEvent event) {
		List<Object> details = event.getOldOperData();
		ModifyAuditorStatusParam param= (ModifyAuditorStatusParam) event.getOperData();
		GrantLog grantLog = event.getOperLog();
		Long logId = null;
		if(null != grantLog){
			logId = grantLog.getGrantLogId();
			grantLogResources.save(grantLog);
		}
		User u = null;
		List<LogDetail> logDetails = null;
		LogDetail logDetail = null;
		if(ListUntils.isNotNull(details) && null != param){
			logDetails = new ArrayList<LogDetail>();
			for(Object o:details){
				if(null != o){
					u = (User)o;
					logDetail = new LogDetail();
					logDetail.setColumnName("disabled");
					logDetail.setCurrentValue(param.getState().equals("0")?"启用":"禁用");
					logDetail.setLogDetailId(idGenerator.next());
					logDetail.setLogId(logId);
					logDetail.setOriginValue(u.isDisabled()?"禁用":"启用");
					logDetail.setRemarks("锁定状态");
					logDetail.setTableName("t_4a_actors");
//					logDetail.setGrantOrgId(u.getOrgId());
//					logDetail.setGrantOrgName(u.getOrgName());
					logDetail.setGrantRelName(u.getName());
					logDetail.setGrantUserId(u.getId());
					logDetails.add(logDetail);
				}
			}
			logDetailResources.save(logDetails);
		}
	}
}
