package com.ido85.party.aaaa.mgmt.security.authentication.application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ido85.party.aaaa.mgmt.business.dto.ExportAuthorizationCodeParam;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.AuditorDto;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.AuditorQueryParam;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.CancellationAuditorParam;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.InAddSecurityUserDto;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.ModifyAuditorStatusParam;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.SetAuditorManageScopeParam;

public interface AuditorApplication {

	/**
	 * 开通审计员
	 * @param param
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> addAuditor(List<InAddSecurityUserDto> param,HttpServletRequest request) throws Exception;

	/**
	 * 审计员查询
	 * @param param
	 * @return
	 */
	List<AuditorDto> securityAuditorQuery(AuditorQueryParam param);

	/**
	 * 审计员数量
	 * @param param
	 * @return
	 */
	Long securityAuditorQueryCnt(AuditorQueryParam param);

	/**
	 * 审计员状态修改
	 * @param param
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> modifyAuditorState(ModifyAuditorStatusParam param,HttpServletRequest request) throws Exception;

	/**
	 * 审计员注销
	 * @param param
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> cancellationSecurityUser(CancellationAuditorParam param,HttpServletRequest request) throws Exception;

	/**
	 * 导出业务管理员授权码
	 * @param param
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws Exception 
	 */
	String exportAuthorizationCode(ExportAuthorizationCodeParam param,HttpServletRequest request) throws FileNotFoundException, IOException, Exception;

	/**
	 * 设置范围
	 * @param param
	 */
	Map<String,String> setManageScope(List<SetAuditorManageScopeParam> param,HttpServletRequest request);

}
