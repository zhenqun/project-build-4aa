package com.ido85.party.aaaa.mgmt.security.authentication.application;

import com.ido85.party.aaaa.mgmt.dto.LogDto;
import com.ido85.party.aaaa.mgmt.dto.LogQueryParam;
import com.ido85.party.aaaa.mgmt.internet.dto.InLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.InRegisterLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutRegisterLogQueryDto;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.SecurityLog;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.OutGrantLogDetailDto;

import java.util.List;

public interface SecurityLogApplication {

	/**
	 * 增加日志
	 * @param logDto
	 */
	void addLog(SecurityLog logDto);

	/**
	 * 授权日志查询
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<LogDto> grantLogQuery(LogQueryParam param) throws Exception;

	/**
	 * 授权日志查询总数
	 * @param param
	 * @return
	 */
	Long grantLogQueryCount(LogQueryParam param) throws Exception;

	/**
	 * 获取批量授权日志详情
	 * @param logId
	 * @return
	 * @throws Exception
	 */
	List<OutGrantLogDetailDto> queryGrantLogDetail(String logId) throws Exception;

	/**
	 * 查询注册日志
	 * @param in
	 * @return
	 * @throws Exception
	 */
	List<OutRegisterLogQueryDto> queryRegisterLog(InRegisterLogQueryDto in) throws Exception;
	/**
	 * 查询注册日志总条数
	 * @param in
	 * @return
	 * @throws Exception
	 */
	Long queryRegisterLogCount(InRegisterLogQueryDto in) throws Exception;

	/**
	 * 查询登陆日志
	 * @param in
	 * @return
	 * @throws Exception
	 */
	List<OutLoginLogQueryDto> queryLoginLog(InLoginLogQueryDto in) throws Exception;

	/**
	 * 查询登陆日志总条数
	 * @param in
	 * @return
	 * @throws Exception
	 */
	Long queryLoginLogCount(InLoginLogQueryDto in) throws Exception;
}
