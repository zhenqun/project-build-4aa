/**
 * 
 */
package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.business.application.BusinessUserApplication;
import com.ido85.party.aaaa.mgmt.business.domain.BusinessUser;
import com.ido85.party.aaaa.mgmt.business.dto.*;
import com.ido85.party.aaaa.mgmt.dto.BusinessUserRoleDto;
import com.ido85.party.aaaa.mgmt.dto.BusinessUserRoleQueryParam;
import com.ido85.party.aaaa.mgmt.dto.CheckRoleParam;
import com.ido85.party.aaaa.mgmt.dto.ResetVpnPassword;
import com.ido85.party.aaaa.mgmt.dto.base.OutBasePageDto;
import com.ido85.party.aaaa.mgmt.security.authentication.application.CommonApplication;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
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
 * 
 * @author rongxj
 *
 */
@RestController
public class BusinessController {
	
	@Inject
	private BusinessUserApplication userApplication;

	@Inject
	private CommonApplication commonApp;

	@Inject
	private PasswordEncoder encoder;
	
	@Value("${security.defaultPassword}")
	private String defaultPassword;
	
	
	/**
	 * 开通业务管理员
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/business/addBusinessUser", method={RequestMethod.POST})
	@ResponseBody
	@Transactional
	public Map<String,String> addBusinessUser(@RequestBody @Valid List<AddBusinessUser> param,HttpServletRequest request){
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
		Map<String,String> result = userApplication.addBusinessUser(param,request);
		return result;
	}
	
	/**
	 * 业务管理员查询
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/business/businessUserQuery", method={RequestMethod.POST})
	@ResponseBody
	public OutBasePageDto<BusinessUserDto> businessUserQuery(@RequestBody BusinessUserQueryParam param){
		if(null == param.getPageNo()||param.getPageNo().intValue()<=0){
			param.setPageNo(1);
		}
		if( null == param.getPageSize()){
			param.setPageSize(20);
		}
		List<BusinessUserDto> userlist = userApplication.getBusinessUserByCondition(param);
		Long cnt = userApplication.getBusinessUserCntByCondition(param);
		OutBasePageDto<BusinessUserDto> page = new OutBasePageDto<BusinessUserDto>();
		page.setCount(cnt);
		page.setData(userlist);
		page.setPageNo(param.getPageNo()); 
		page.setPageSize(param.getPageSize());
		return page;
	}

	/**
	 * 业务管理员角色查询
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/business/businessUserRoleQuery", method={RequestMethod.POST})
	@ResponseBody
	public List<BusinessUserRoleDto> businessUserRoleQuery(@RequestBody BusinessUserRoleQueryParam param){
		List<BusinessUserRoleDto> list = userApplication.businessUserRoleQuery(param);
		return list;
	}
	
	/**
	 * 业务管理员状态修改
	 * @param status
	 * @param userId
	 * @return
	 */
	@RequestMapping(path="/manage/business/modifyBusinessUserState", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> modifyBusinessUserStatus(@RequestBody @Valid ModifyBusinessUserStatusParam param,HttpServletRequest request){
		Map<String,String> result = userApplication.modifyBusinessUserStatus(param,request);
		return result;
	}
	
	/**
	 * 业务管理员注销
	 * @param status
	 * @param userId
	 * @return
	 */
	@RequestMapping(path="/manage/business/cancellationBusinessUser", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> cancellationBusinessUser(@RequestBody @Valid CancellationBusinessUserParam param,HttpServletRequest request){
		Map<String,String> result = userApplication.cancellationBusinessUser(param,request);
		return result;
	}
	
	/**
	 * 为业务管理员设置管理范围（批量）
	 * @return
	 */
	@RequestMapping(path="/manage/business/setManageScope", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> setManageScope(@RequestBody @Valid SetManageScopeParam param,HttpServletRequest request){
		Map<String,String> result = userApplication.setManageScope(param,request);
		return result;
	}
	
	/**
	 * 给业务管理员授权
	 * @return
	 */
	@RequestMapping(path="/manage/business/grantBusinessUser", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> grantBusinessUser(@Valid @RequestBody List<GrantBusinessUserParam> param,HttpServletRequest request){
		Map<String,String> result = userApplication.grantBusinessUser(param,request);
		return result;
	}
	
	/**
	 * 重置密码
	 * @return
	 */
	@RequestMapping(path="/manage/resetBusinessUserPwd/{userId}",method=RequestMethod.GET)
	public boolean resetBusinessUserPwd(@PathVariable("userId")String userId){
		BusinessUser user = userApplication.getBusinessUserById(userId);
		if(null == user){
			return false;
		}
		user.setPassword(encoder.encode(defaultPassword));
		userApplication.saveBusinessUser(user);
		return true;
	}
	
	/**
	 * 导出业务管理员授权码
	 * @return
	 */
	@RequestMapping(path="/manage/business/exportAuthorizationCode", method={RequestMethod.POST})
	@ResponseBody
	public String exportAuthorizationCode(@Valid @RequestBody ExportAuthorizationCodeParam param,HttpServletRequest request){
		String result;
		try {
			result = userApplication.exportAuthorizationCode(param,request);
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
	 * 根据用户id查询应用角色详情 TODO
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/business/clientRoleQuery", method={RequestMethod.POST})
	@ResponseBody
	public List<ClientRolesDto> clientRoleQuery(@RequestBody ClientRoleQueryParam param){
		List<ClientRolesDto> list = userApplication.clientRoleQuery(param.getBusinessUserId(),param.getClientId());
		return list;
	}
	
	/**
	 * 检测角色授权是否合法
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/business/checkRole", method={RequestMethod.POST})
	@ResponseBody
	public boolean checkRole(@RequestBody CheckRoleParam param){
		return userApplication.checkRole(param);
	}

	/**
	 * 检测撤销用户在改应用下的管理范围
	 */
	@RequestMapping(path="/manage/business/checkCancelUser",method = RequestMethod.POST)
	@ResponseBody
	public List<String> checkcancelBusiUserClient(@Valid @RequestBody CancelBusiUserClientParam param){
		return userApplication.checkcancelBusiUserClient(param);
	}

	/**
	 * 撤销用户在改应用下的管理范围
	 */
	@RequestMapping(path="/manage/business/cancelUserClient",method = RequestMethod.POST)
	@ResponseBody
	public boolean cancelBusiUserClient(@Valid @RequestBody CancelBusiUserClientParam param){
		return userApplication.cancelBusiUserClient(param);
	}

	@RequestMapping(path = "/manage/business/resetVpnPassword/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public boolean resetVpnPassword(@PathVariable("userId") String userId) {
		ResetVpnPassword param = new ResetVpnPassword();
		param.setUserId(userId);
		param.setType("3");
		return commonApp.resetVpnPassword(param);
	}
}
