package com.ido85.party.aaaa.mgmt.security.authentication.application.impl;

import com.ido85.party.aaaa.mgmt.dto.LogDto;
import com.ido85.party.aaaa.mgmt.dto.LogQueryParam;
import com.ido85.party.aaaa.mgmt.internet.dto.InLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.InRegisterLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutRegisterLogQueryDto;
import com.ido85.party.aaaa.mgmt.security.authentication.application.SecurityLogApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.SecurityLog;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.OutGrantLogDetailDto;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.SecurityLogResources;
import com.ido85.party.aaaa.mgmt.security.common.BaseApplication;
import com.ido85.party.aaaa.mgmt.security.utils.DateUtils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
public class SecurityLogApplicationImpl extends BaseApplication implements SecurityLogApplication {
	@PersistenceContext(unitName = "admin")
	private EntityManager adminEntity;
	
	@Inject
	private SecurityLogResources securityLogResources;
	
	/**
	 * 增加日志
	 */
	public void addLog(SecurityLog log) {
		securityLogResources.save(log);
	}

	public List<LogDto> grantLogQuery(LogQueryParam param) throws Exception {
		StringBuffer sql = new StringBuffer(" select new com.ido85.party.aaaa.mgmt.dto.LogDto(" +
				" t.hasDetail, t.hostName, t.idCard, t.createDate, t.grantLogId, t.operateBy, t.grantIp, t.operateRelName," +
				" t.mac, t.operateOrgName, t.operateRelName, t.businessName, t.url " +
				") from GrantLog t where 1 = 1 ");

		Map<String, Object> params = new HashMap<>();

		if (StringUtils.isNotBlank(param.getLogOperator())) {
			sql.append(" and t.operateRelName like :operateRelName ");
			params.put("operateRelName", "%" + param.getLogOperator() + "%");
		}

		if (StringUtils.isNotBlank(param.getLogType())) {
			sql.append(" and t.operateType = :operateType ");
			params.put("operateType", param.getLogType());
		}

		if (StringUtils.isNotBlank(param.getStartDate()) && StringUtils.isNotBlank(param.getEndDate())) {
			sql.append(" and t.createDate >= :startDate and t.createDate <= :endDate ");
			params.put("startDate", DateUtils.parseDate(param.getStartDate()));
			params.put("endDate", DateUtils.parseDate(param.getEndDate()));
		}

		return this.queryPageEntityNodes(sql.toString(), params, param.getPageSize(), param.getPageNo(), LogDto.class);
	}

	@Override
	public Long grantLogQueryCount(LogQueryParam param) throws Exception {
		StringBuffer sql = new StringBuffer(" select  count(t) from GrantLog t where 1 = 1 ");

		Map<String, Object> params = new HashMap<>();

		if (StringUtils.isNotBlank(param.getLogOperator())) {
			sql.append(" and t.operateRelName like :operateRelName ");
			params.put("operateRelName", "%" + param.getLogOperator() + "%");
		}

		if (StringUtils.isNotBlank(param.getLogType())) {
			sql.append(" and t.operateType = :operateType ");
			params.put("operateType", param.getLogType());
		}

		if (StringUtils.isNotBlank(param.getStartDate()) && StringUtils.isNotBlank(param.getEndDate())) {
			sql.append(" and t.createDate >= :startDate and t.createDate <= :endDate ");
			params.put("startDate", DateUtils.parseDate(param.getStartDate()));
			params.put("endDate", DateUtils.parseDate(param.getEndDate()));
		}

		return this.queryPageEntityCount(sql.toString(), params);
	}

	@Override
	public List<OutGrantLogDetailDto> queryGrantLogDetail(String logId) throws Exception {
		StringBuffer sql = new StringBuffer(" select new com.ido85.party.aaaa.mgmt.security.authentication.dto.OutGrantLogDetailDto(" +
				" t.currentValue, t.grantOrgName, t.originValue, t.grantRelName, t.remarks " +
				") from LogDetail t where t.logId = :logId ");

		Query query = entity.createQuery(sql.toString(), OutGrantLogDetailDto.class);
		query.setParameter("logId", StringUtils.toLong(logId));

		return query.getResultList();
	}

	@Override
	public List<OutRegisterLogQueryDto> queryRegisterLog(InRegisterLogQueryDto in) throws Exception {
		StringBuffer sql = new StringBuffer(" select new com.ido85.party.aaaa.mgmt.internet.dto.OutRegisterLogQueryDto(" +
				" t.hostName, t.idCard, t.registerDate, t.relName, t.registerIp, t.userName, t.mac, t.orgName, t.relName, t.registerTypeName " +
				") from RegisterLog t where 1 = 1 ");

		Map<String, Object> params = new HashMap<>();

		if (StringUtils.isNotBlank(in.getLogOperator())) {
			sql.append(" and t.relName like :relName ");
			params.put("relName", "%" + in.getLogOperator() + "%");
		}

		if (StringUtils.isNotBlank(in.getResult())) {
			sql.append(" and t.registerType = :registerType ");
			params.put("registerType", in.getResult());
		}

		if (StringUtils.isNotBlank(in.getStartDate()) && StringUtils.isNotBlank(in.getEndDate())) {
			sql.append(" and t.registerDate >= :startDate and t.registerDate <= :endDate ");
			params.put("startDate", DateUtils.parseDate(in.getStartDate()));
			params.put("endDate", DateUtils.parseDate(in.getEndDate()));
		}
		return this.queryPageEntityNodes(sql.toString(), params, in.getPageSize(), in.getPageNo(), OutRegisterLogQueryDto.class);
	}

	@Override
	public Long queryRegisterLogCount(InRegisterLogQueryDto in) throws Exception {
		StringBuffer sql = new StringBuffer(" select count(t) from RegisterLog t where 1 = 1 ");

		Map<String, Object> params = new HashMap<>();

		if (StringUtils.isNotBlank(in.getLogOperator())) {
			sql.append(" and t.relName like :relName ");
			params.put("relName", "%" + in.getLogOperator() + "%");
		}

		if (StringUtils.isNotBlank(in.getResult())) {
			sql.append(" and t.registerType = :registerType ");
			params.put("registerType", in.getResult());
		}

		if (StringUtils.isNotBlank(in.getStartDate()) && StringUtils.isNotBlank(in.getEndDate())) {
			sql.append(" and t.registerDate >= :startDate and t.registerDate <= :endDate ");
			params.put("startDate", DateUtils.parseDate(in.getStartDate()));
			params.put("endDate", DateUtils.parseDate(in.getEndDate()));
		}
		return this.queryPageEntityCount(sql.toString(), params);
	}
	@Override
	public List<OutLoginLogQueryDto> queryLoginLog(InLoginLogQueryDto in) throws Exception {
		StringBuffer sql = new StringBuffer(" select new com.ido85.party.aaaa.mgmt.internet.dto.OutLoginLogQueryDto(" +
				" t.hostName, t.idCard, t.loginDate, t.relName, t.loginIp, t.loginName, t.mac, t.orgName, t.relName, t.loginResult " +
				") from LoginLog t where 1 = 1 ");

		Map<String, Object> params = new HashMap<>();

		if (StringUtils.isNotBlank(in.getLogOperator())) {
			sql.append(" and t.relName like :relName ");
			params.put("relName", "%" + in.getLogOperator() + "%");
		}

		if (StringUtils.isNotBlank(in.getResult())) {
			sql.append(" and t.loginType = :loginType ");
			params.put("loginType", in.getResult());
		}

		if (StringUtils.isNotBlank(in.getStartDate()) && StringUtils.isNotBlank(in.getEndDate())) {
			sql.append(" and t.loginDate >= :startDate and t.loginDate <= :endDate ");
			params.put("startDate", DateUtils.parseDate(in.getStartDate()));
			params.put("endDate", DateUtils.parseDate(in.getEndDate()));
		}
		sql.append(" order by t.loginDate desc");
		return this.queryPageEntityNodes(sql.toString(), params, in.getPageSize(), in.getPageNo(), OutLoginLogQueryDto.class);
	}

	@Override
	public Long queryLoginLogCount(InLoginLogQueryDto in) throws Exception {
		StringBuffer sql = new StringBuffer(" select count(t) from LoginLog t where 1 = 1 ");

		Map<String, Object> params = new HashMap<>();

		if (StringUtils.isNotBlank(in.getLogOperator())) {
			sql.append(" and t.relName like :relName ");
			params.put("relName", "%" + in.getLogOperator() + "%");
		}

		if (StringUtils.isNotBlank(in.getResult())) {
			sql.append(" and t.loginType = :loginType ");
			params.put("loginType", in.getResult());
		}

		if (StringUtils.isNotBlank(in.getStartDate()) && StringUtils.isNotBlank(in.getEndDate())) {
			sql.append(" and t.loginDate >= :startDate and t.loginDate <= :endDate ");
			params.put("startDate", DateUtils.parseDate(in.getStartDate()));
			params.put("endDate", DateUtils.parseDate(in.getEndDate()));
		}
		return this.queryPageEntityCount(sql.toString(), params);
	}

}
