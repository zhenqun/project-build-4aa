package com.ido85.party.aaaa.mgmt.security.dbsync.listener;

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

import com.ido85.party.aaaa.mgmt.security.authentication.domain.GrantLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.LogDetail;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.GrantLogResources;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.LogDetailResources;
import com.ido85.party.aaaa.mgmt.security.constants.LogConstants;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.GrantAuditorEvent;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;


/**
 * @ClassName: GrantAuditorListener
 * @Description: TODO
 * @author: yin
 */
@Named
public class GrantAuditorListener implements ApplicationListener<GrantAuditorEvent> {

	@Inject
	private GrantLogResources grantLogResources;
	
	@Inject
	private LogDetailResources logDetailResources;
	
	@Override
	@Transactional
	@Async
	public void onApplicationEvent(GrantAuditorEvent event) {
		try {
			GrantLog grantLog = event.getGrantLog();
			if (null != grantLog) {
				grantLogResources.save(grantLog);
			}
			List<LogDetail> list = event.getLogDetailsList();
			if(ListUntils.isNotNull(list)){
				logDetailResources.save(list);
			}
		} catch (Exception e) {
			
		} 
	}
}
