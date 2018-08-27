package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.business.dto.ExportAuthorizationCodeParam;
import com.ido85.party.aaaa.mgmt.dto.ResetVpnPassword;
import com.ido85.party.aaaa.mgmt.dto.base.OutBasePageDto;
import com.ido85.party.aaaa.mgmt.security.authentication.application.CommonApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.SecurityUserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by fire on 2017/2/22.
 */
@RestController
public class SecurityUserController {
	@Inject
	private SecurityUserApplication securityUserApp;

	@Inject
	private CommonApplication commonApp;

	/**
	 * 开通安全管理员
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/security/addSecurityUser", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> addSecurityUser(@Valid @RequestBody List<InAddSecurityUserDto> param,HttpServletRequest request)throws Exception{
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
		Map<String,String> result = securityUserApp.addSecurityUser(param,request);
		return result;
	}


	/**
	 * 查询安全管理员
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/security/securityUserQuery", method={RequestMethod.POST})
	@ResponseBody
	public OutBasePageDto<SecurityUserDto> securityUserQuery(@Valid @RequestBody SecurityUserQueryParam param)throws Exception{
		if(null == param.getPageNo()||param.getPageNo().intValue()<=0){
			param.setPageNo(1);
		}
		if( null == param.getPageSize()){
			param.setPageSize(20);
		}
		List<SecurityUserDto> userlist = securityUserApp.securityUserQuery(param);
		Long cnt = securityUserApp.securityUserQueryCnt(param);
		OutBasePageDto<SecurityUserDto> page = new OutBasePageDto<SecurityUserDto>();
		page.setCount(cnt);
		page.setData(userlist);
		page.setPageNo(param.getPageNo()); 
		page.setPageSize(param.getPageSize());
		return page;
	}

	/**
	 * 安全员状态修改
	 * @param status
	 * @param userId
	 * @return
	 */
	@RequestMapping(path="/manage/security/modifySecurityUserState", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> modifySecurityUserState(@Valid @RequestBody ModifySecurityUserStatusParam param,HttpServletRequest request){
		Map<String,String> result = securityUserApp.modifySecurityUserState(param,request);
		return result;
	}
	
	/**
	 * 安全员注销
	 * @param status
	 * @param userId
	 * @return
	 */
	@RequestMapping(path="/manage/security/cancellationSecurityUser", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> cancellationSecurityUser(@Valid @RequestBody CancellationSecurityUserParam param,HttpServletRequest request){
		Map<String,String> result = securityUserApp.cancellationSecurityUser(param,request);
		return result;
	}
	
	/**
	 * 为安全员设置管理范围（批量）
	 * @return
	 */
	@RequestMapping(path="/manage/security/setManageScope", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> setManageScope(@RequestBody @Valid List<SetSecurityUserManageScopeParam> param,HttpServletRequest request){
		Map<String,String> result = securityUserApp.setManageScope(param,request);
		return result;
	}

	/**
	 * 导出安全员授权码
	 * @return
	 */
	@RequestMapping(path="/manage/security/exportAuthorizationCode", method={RequestMethod.POST})
	@ResponseBody
	public String exportAuthorizationCode(@Valid @RequestBody ExportAuthorizationCodeParam param,HttpServletRequest request){
		String result;
		try {
			result = securityUserApp.exportAuthorizationCode(param,request);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * `
	 */
	@RequestMapping(path="/manage/security/checkCancelUser",method = RequestMethod.POST)
	@ResponseBody
	public List<String> checkCancelSecUser(@Valid @RequestBody  CancelSecUserClientParam param){
		return securityUserApp.checkCancelSecUser(param);
	}

	/**
	 * 撤销用户在改应用下的管理范围
	 */
	@RequestMapping(path="/manage/security/cancelUserClient",method = RequestMethod.POST)
	@ResponseBody
	public boolean cancelSecUserClient(@Valid @RequestBody CancelSecUserClientParam param){
		return securityUserApp.cancelSecUserClient(param);
	}

	@RequestMapping(path = "/manage/security/resetVpnPassword/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public boolean resetVpnPassword(@PathVariable("userId") String userId) {
		ResetVpnPassword param = new ResetVpnPassword();
		param.setUserId(userId);
		param.setType("1");
		return commonApp.resetVpnPassword(param);
	}
	//---------------------------------2.0-------------------------
	/**
	 * 为安全员设置应用管理范围
	 * @param param
	 * @return
	 */
//	@RequestMapping(path="/manage/security/addSecurityUser", method={RequestMethod.POST})
//	@ResponseBody
//	public Map<String,String> addSecurityUser(@Valid @RequestBody List<InAddSecurityUserDto> param,HttpServletRequest request)throws Exception{}
	
}
