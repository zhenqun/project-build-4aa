package com.ido85.party.aaaa.mgmt.service.impl;

import com.ido85.party.aaaa.mgmt.business.application.BusinessLogApplication;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.LogDto;
import com.ido85.party.aaaa.mgmt.dto.LogQueryParam;
import com.ido85.party.aaaa.mgmt.dto.base.OutBasePageDto;
import com.ido85.party.aaaa.mgmt.internet.application.InternetLogApplication;
import com.ido85.party.aaaa.mgmt.internet.dto.InLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.InRegisterLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutRegisterLogQueryDto;
import com.ido85.party.aaaa.mgmt.security.authentication.application.SecurityLogApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.SecurityLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.OutGrantLogDetailDto;
import com.ido85.party.aaaa.mgmt.service.LogService;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
@Named
public class LogServiceImpl implements LogService {

	@Inject
	private SecurityLogApplication securityLogApplication;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Inject
	private BusinessLogApplication businessLogApplication;
	
	@Inject
	private InternetLogApplication internetLogApplication;
	
	/**
	 * 增加日志
	 */
	public void addLog(SecurityLog log) {
		
		//获取当前登录人
		User user = (User) SecurityContextHolder.getContext()
			    .getAuthentication()
			    .getPrincipal();
		log.setCreateBy(user.getId());
		log.setCreateDate(new Date());
		log.setIdCard(user.getIdCard());
		log.setLogId(idGenerator.next());
		log.setMobile(user.getTelePhone());
//		log.setOrgId(user.getOrgId());
//		log.setOrgName(user.getOrgName());
		log.setRelName(user.getName());
		log.setUserId(user.getId());
		log.setUsername(user.getUsername());
		securityLogApplication.addLog(log);
		
	}

	/**
	 * 日志获取
	 * @param param
	 * @return
	 */
	public List<LogDto> logQuery(LogQueryParam param) throws Exception {
		return securityLogApplication.grantLogQuery(param);
	}

	/**
	 * 日志条数
	 * @param param
	 * @return
	 */
	public Long logQueryCnt(LogQueryParam param) throws Exception {
		return securityLogApplication.grantLogQueryCount(param);
	}

	@Override
	public List<OutGrantLogDetailDto> queryGrantLogDetail(String logId) throws Exception {
		return securityLogApplication.queryGrantLogDetail(logId);
	}

	@Override
	public OutBasePageDto<OutRegisterLogQueryDto> queryRegisterLog(InRegisterLogQueryDto in) throws Exception {
		OutBasePageDto<OutRegisterLogQueryDto> res = new OutBasePageDto<>();

		if("0".equals(in.getLogFrom())){//互联网
			res.setData(internetLogApplication.queryRegisterLog(in));
			res.setCount(internetLogApplication.queryRegisterLogCount(in));
		}else if ("1".equals(in.getLogFrom())){//vpn
			res.setData(businessLogApplication.queryRegisterLog(in));
			res.setCount(businessLogApplication.queryRegisterLogCount(in));
		}else if ("2".equals(in.getLogFrom())){//安全管理
			res.setData(securityLogApplication.queryRegisterLog(in));
			res.setCount(securityLogApplication.queryRegisterLogCount(in));
		}

		res.setPageSize(in.getPageSize());
		res.setPageNo(in.getPageNo());

		return res;
	}

	@Override
	public OutBasePageDto<OutLoginLogQueryDto> queryLoginLog(InLoginLogQueryDto in) throws Exception {
		OutBasePageDto<OutLoginLogQueryDto> res = new OutBasePageDto<>();

		if("0".equals(in.getLogFrom())){//互联网
			res.setData(internetLogApplication.queryLoginLog(in));
			res.setCount(internetLogApplication.queryLoginLogCount(in));
		}else if ("1".equals(in.getLogFrom())){//vpn
			res.setData(businessLogApplication.queryLoginLog(in));
			res.setCount(businessLogApplication.queryLoginLogCount(in));
		}else if ("2".equals(in.getLogFrom())){//安全管理
			res.setData(securityLogApplication.queryLoginLog(in));
			res.setCount(securityLogApplication.queryLoginLogCount(in));
		}

		res.setPageSize(in.getPageSize());
		res.setPageNo(in.getPageNo());

		return res;
	}

}
