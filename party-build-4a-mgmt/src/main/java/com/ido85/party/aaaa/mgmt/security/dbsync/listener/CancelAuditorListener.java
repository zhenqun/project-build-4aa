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
import com.ido85.party.aaaa.mgmt.security.authentication.dto.CancellationAuditorParam;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.GrantLogResources;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.LogDetailResources;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.CancelAuditorEvent;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;


/**
 * @ClassName: GrantAuditorListener
 * @Description: TODO
 * @author: yin
 */
@Named
public class CancelAuditorListener implements ApplicationListener<CancelAuditorEvent> {

	@Inject
	private GrantLogResources grantLogResources;
	
	@Inject
	private LogDetailResources logDetailResources;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Override
	public void onApplicationEvent(CancelAuditorEvent event) {
		List<User> userList = event.getOldOperData();
		CancellationAuditorParam param= (CancellationAuditorParam) event.getOperData();
		GrantLog grantLog = event.getOperLog();
		Long logId = null;
		if(null != grantLog){
			logId = grantLog.getGrantLogId();
			grantLogResources.save(grantLog);
		}
		List<LogDetail> logDetails = null;
		LogDetail logDetail = null;
		if(ListUntils.isNotNull(userList) && null != param){
			logDetails = new ArrayList<LogDetail>();
			for(User o:userList){
				if(null != o){
					logDetail = new LogDetail();
					logDetail.setColumnName("is_account_expired");
					logDetail.setCurrentValue("注销");
					logDetail.setLogDetailId(idGenerator.next());
					logDetail.setLogId(logId);
					logDetail.setOriginValue("未注销");
					logDetail.setRemarks("注销状态");
					logDetail.setTableName("t_4a_actors");
//					logDetail.setGrantOrgId(o.getOrgId());
//					logDetail.setGrantOrgName(o.getOrgName());
					logDetail.setGrantRelName(o.getName());
					logDetail.setGrantUserId(o.getId());
					logDetails.add(logDetail);
				}
			}
			logDetailResources.save(logDetails);
		}
	}
}
