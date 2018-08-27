package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.business.dto.ExportAuthorizationCodeParam;
import com.ido85.party.aaaa.mgmt.dto.ResetVpnPassword;
import com.ido85.party.aaaa.mgmt.dto.base.OutBasePageDto;
import com.ido85.party.aaaa.mgmt.security.authentication.application.AuditorApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.CommonApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by fire on 2017/2/22.
 */
@RestController
public class AuditorController {
	@Inject
	private AuditorApplication auditorApp;

	@Inject
	private CommonApplication commonApp;
	/**
	 * 开通审计员
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/auditor/addAuditorUser", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> addAuditor(@Valid @RequestBody List<InAddSecurityUserDto> param,HttpServletRequest request)throws Exception{
		if (null != param) {
			param.stream()
				.forEach(x -> {
					if (null != x) {
						String idCard = x.getIdCard();
						if (StringUtils.isNotBlank(idCard)) {
							x.setIdCard(idCard.toUpperCase());
						}
					}
				});
		}
		Map<String,String> result = auditorApp.addAuditor(param,request);
		return result;
	}


	/**
	 * 查询审计员
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/auditor/auditorUserQuery", method={RequestMethod.POST})
	@ResponseBody
	public OutBasePageDto<AuditorDto> securityAuditorQuery(@Valid @RequestBody AuditorQueryParam param)throws Exception{
		if(null == param.getPageNo()||param.getPageNo().intValue()<=0){
			param.setPageNo(1);
		}
		if( null == param.getPageSize()){
			param.setPageSize(20);
		}
		List<AuditorDto> userlist = auditorApp.securityAuditorQuery(param);
		Long cnt = auditorApp.securityAuditorQueryCnt(param);
		OutBasePageDto<AuditorDto> page = new OutBasePageDto<AuditorDto>();
		page.setCount(cnt);
		page.setData(userlist);
		page.setPageNo(param.getPageNo()); 
		page.setPageSize(param.getPageSize());
		return page;
	}

	/**
	 * 审计员状态修改
	 * @param status
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(path="/manage/auditor/modifyAuditorUserState", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> modifyAuditorState(@Valid @RequestBody ModifyAuditorStatusParam param,HttpServletRequest request) throws Exception{
		Map<String,String> result = auditorApp.modifyAuditorState(param,request);
		return result;
	}
	
	/**
	 * 审计员注销
	 * @param status
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(path="/manage/auditor/cancellationAuditorUser", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> cancellationSecurityUser(@Valid @RequestBody CancellationAuditorParam param,HttpServletRequest request) throws Exception{
		Map<String,String> result = auditorApp.cancellationSecurityUser(param,request);
		return result;
	}
	
	/**
	 * 为审计员设置查看范围（批量）
	 */
	@RequestMapping(path="/manage/auditor/setManageScope", method={RequestMethod.POST})
	@ResponseBody
	@Transactional
	public Map<String, String> setManageScope(@Valid @RequestBody List<SetAuditorManageScopeParam> param,HttpServletRequest request) {
		Map<String,String> result = auditorApp.setManageScope(param,request);
		return result;
	}
	
	/**
	 * 导出业务管理员授权码
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(path="/manage/auditor/exportAuthorizationCode", method={RequestMethod.POST})
	@ResponseBody
	public String exportAuthorizationCode(@Valid @RequestBody ExportAuthorizationCodeParam param,HttpServletRequest request) throws Exception{
		String result;
		try {
			result = auditorApp.exportAuthorizationCode(param,request);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	@RequestMapping(path = "/manage/auditor/resetVpnPassword/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public boolean resetVpnPassword(@PathVariable("userId") String userId) {
		ResetVpnPassword param = new ResetVpnPassword();
		param.setUserId(userId);
		param.setType("2");
		return commonApp.resetVpnPassword(param);
	}
}
