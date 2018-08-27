package com.ido85.party.aaaa.mgmt.business.application;

import com.ido85.party.aaaa.mgmt.internet.dto.InLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.InRegisterLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutRegisterLogQueryDto;

import java.util.List;

public interface BusinessLogApplication {
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
