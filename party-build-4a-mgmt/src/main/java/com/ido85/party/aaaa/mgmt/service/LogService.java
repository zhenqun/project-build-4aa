package com.ido85.party.aaaa.mgmt.service;

import com.ido85.party.aaaa.mgmt.dto.LogDto;
import com.ido85.party.aaaa.mgmt.dto.LogQueryParam;
import com.ido85.party.aaaa.mgmt.dto.base.OutBasePageDto;
import com.ido85.party.aaaa.mgmt.internet.dto.InLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.InRegisterLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutRegisterLogQueryDto;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.SecurityLog;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.OutGrantLogDetailDto;

import java.util.List;

public interface LogService {

	/**
	 * 增加日志
	 * @param logDto
	 */
	void addLog(SecurityLog logDto);

	/**
	 * 日志获取
	 * @param param
	 * @return
	 */
	List<LogDto> logQuery(LogQueryParam param) throws Exception;

	/**
	 * 日志条数
	 * @param param
	 * @return
	 */
	Long logQueryCnt(LogQueryParam param) throws Exception;

	/**
	 * 获取批量授权日志详情
	 * @param logId
	 * @return
	 * @throws Exception
	 */
	List<OutGrantLogDetailDto> queryGrantLogDetail(String logId) throws Exception;

	/**
	 *
	 * 注册日志查询
	 * @param in
	 * @return
	 * @throws Exception
	 */
	OutBasePageDto<OutRegisterLogQueryDto> queryRegisterLog(InRegisterLogQueryDto in) throws Exception;

	/**
	 *
	 * 登陆日志查询
	 * @param in
	 * @return
	 * @throws Exception
	 */
	OutBasePageDto<OutLoginLogQueryDto> queryLoginLog(InLoginLogQueryDto in) throws Exception;

}
