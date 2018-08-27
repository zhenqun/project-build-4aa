package com.ido85.party.aaaa.mgmt.security.dbsync.factory;


import com.ido85.party.aaaa.mgmt.security.constants.LogConstants;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.AddAuditorEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.AddBusinessUserEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.AddSecurityUserEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.CancelAuditorEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.CancelBusinessUserEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.CancelSecurityUserEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.ExportAuditorCodeEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.ExportBusinessUserCodeEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.ExportSecurityUserCodeEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.GrantBusinessUserEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.ModAuditorStateEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.ModBusinessUserStateEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.ModSecurityUserStateEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.OperLogEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.SetAuditorEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.SetBusinessUserEvent;
import com.ido85.party.aaaa.mgmt.security.dbsync.events.SetSecurityUserEvent;

public class EventFactory {

	@SuppressWarnings({ "rawtypes"})
	public static  OperLogEvent createEvent(String addAuditor) {
		//审计员
		if(LogConstants.ADD_AUDITOR.equals(addAuditor)){
			return new AddAuditorEvent(addAuditor);
		}
		if(LogConstants.MOD_AUDITOR_STATE.equals(addAuditor)){
			return new ModAuditorStateEvent(addAuditor);
		}
		if(LogConstants.CANCEL_AUDITOR.equals(addAuditor)){
			return new CancelAuditorEvent(addAuditor);
		}
		if(LogConstants.EXPORT_AUDITOR_CODE.equals(addAuditor)){
			return new ExportAuditorCodeEvent(addAuditor);
		}
		if(LogConstants.SET_AUDITOR_MANAGE.equals(addAuditor)){
			return new SetAuditorEvent(addAuditor);
		}
		//安全员
		if(LogConstants.ADD_SECURITYUSER.equals(addAuditor)){
			return new AddSecurityUserEvent(addAuditor);
		}
		if(LogConstants.MOD_SECURITYUSER_STATE.equals(addAuditor)){
			return new ModSecurityUserStateEvent(addAuditor);
		}
		if(LogConstants.CANCEL_SECURITYUSER.equals(addAuditor)){
			return new CancelSecurityUserEvent(addAuditor);
		}
		if(LogConstants.EXPORT_SECURITYUSER_CODE.equals(addAuditor)){
			return new ExportSecurityUserCodeEvent(addAuditor);
		}
		if(LogConstants.SET_SECURITYUSER_MANAGE.equals(addAuditor)){
			return new SetSecurityUserEvent(addAuditor);
		}
		//业务管理员
		if(LogConstants.ADD_BUSINESSUSER.equals(addAuditor)){
			return new AddBusinessUserEvent(addAuditor);
		}
		if(LogConstants.MOD_BUSINESSUSER_STATE.equals(addAuditor)){
			return new ModBusinessUserStateEvent(addAuditor);
		}
		if(LogConstants.CANCEL_BUSINESSUSER.equals(addAuditor)){
			return new CancelBusinessUserEvent(addAuditor);
		}
		if(LogConstants.EXPORT_BUSINESSUSER_CODE.equals(addAuditor)){
			return new ExportBusinessUserCodeEvent(addAuditor);
		}
		if(LogConstants.SET_BUSINESSUSER_MANAGE.equals(addAuditor)){
			return new SetBusinessUserEvent(addAuditor);
		}
		if(LogConstants.GRANT_BUSINESSUSER.equals(addAuditor)){
			return new GrantBusinessUserEvent(addAuditor);
		}
		return null;
	}
	
	
}
