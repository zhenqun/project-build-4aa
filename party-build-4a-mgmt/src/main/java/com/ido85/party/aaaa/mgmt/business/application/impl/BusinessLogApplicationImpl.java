package com.ido85.party.aaaa.mgmt.business.application.impl;

import com.ido85.party.aaaa.mgmt.business.application.BusinessLogApplication;
import com.ido85.party.aaaa.mgmt.internet.dto.InLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.InRegisterLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutRegisterLogQueryDto;
import com.ido85.party.aaaa.mgmt.security.common.BaseApplication;
import com.ido85.party.aaaa.mgmt.security.utils.DateUtils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;

import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
public class BusinessLogApplicationImpl extends BaseApplication implements BusinessLogApplication {


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
		return this.queryPageEntityNodesForBusiness(sql.toString(), params, in.getPageSize(), in.getPageNo(), OutRegisterLogQueryDto.class);
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
		return this.queryPageEntityCountForBusiness(sql.toString(), params);
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
		return this.queryPageEntityNodesForBusiness(sql.toString(), params, in.getPageSize(), in.getPageNo(), OutLoginLogQueryDto.class);
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
		return this.queryPageEntityCountForBusiness(sql.toString(), params);
	}

}
