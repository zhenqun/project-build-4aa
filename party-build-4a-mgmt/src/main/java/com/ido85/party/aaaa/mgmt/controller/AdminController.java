/**
 * 
 */
package com.ido85.party.aaaa.mgmt.controller;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ido85.party.aaaa.mgmt.business.domain.BusinessUser;
import com.ido85.party.aaaa.mgmt.dto.AdminDto;
import com.ido85.party.aaaa.mgmt.dto.AdminQueryParam;
import com.ido85.party.aaaa.mgmt.dto.ApplicationAllotParam;
import com.ido85.party.aaaa.mgmt.dto.base.OutBasePageDto;
import com.ido85.party.aaaa.mgmt.internet.dto.ApplicationDto;
import com.ido85.party.aaaa.mgmt.security.authentication.application.UserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;


/**
 * 
 * @author rongxj
 *
 */
@RestController
public class AdminController {
	
	@Inject
	private UserApplication userApplication;
	
	@Inject
	private PasswordEncoder encoder;
	
	@Value("${security.defaultPassword}")
	private String defaultPassword;
	
	/**
	 * 管理员查询
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/admin/adminQuery", method={RequestMethod.POST})
	@ResponseBody
	public OutBasePageDto<AdminDto> adminQuery(@RequestBody AdminQueryParam param){
		if(null == param.getPageNo()||param.getPageNo().intValue()<=0){
			param.setPageNo(1);
		}
		if( null == param.getPageSize()){
			param.setPageSize(20);
		}
		List<AdminDto> adminlist = userApplication.getAdminByCondition(param);
		Long cnt = userApplication.getAdminCntByCondition(param);
		OutBasePageDto<AdminDto> page = new OutBasePageDto<AdminDto>();
		page.setCount(cnt);
		page.setData(adminlist);
		page.setPageNo(param.getPageNo()); 
		page.setPageSize(param.getPageSize());
		return page;
	}
	
	/**
	 * 管理员状态修改
	 * @param status
	 * @param userId
	 * @return
	 */
	@RequestMapping(path="/manage/admin/modifyAdminStatus/{userId}/{status}", method={RequestMethod.GET})
	@ResponseBody
	public boolean modifyAdminStatus(@PathVariable("status")Integer status,@PathVariable("userId")Long userId){
		boolean flag = userApplication.modifyAdminStatus(userId,status);
		return flag;
	}
	
	/**
	 * 系统分配
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/admin/applicationAllot", method={RequestMethod.POST})
	@ResponseBody
	public boolean applicationAllot(@Valid @RequestBody ApplicationAllotParam param){
		boolean flag = userApplication.applicationAllot(param);
		return flag;
	}
	
	/**
	 * 重置密码
	 * @param username
	 * @param newPassword
	 * @return
	 */
	@RequestMapping(path="/manage/resetAdminPwd/{userId}",method=RequestMethod.GET)
	public boolean resetAdminPwd(@PathVariable("userId")Long userId){
		User user = userApplication.getAdminById(userId);
		if(null == user){
			return false;
		}
		user.setPassword(encoder.encode(defaultPassword));
		userApplication.saveAdmin(user);
		return true;
	}
	
	/**
	 * 查询应用列表
	 * @param status
	 * @param userId
	 * @return
	 */
	@RequestMapping(path="/manage/admin/applicationQuery", method={RequestMethod.POST})
	@ResponseBody
	public List<ApplicationDto> applicationQuery(){
		List<ApplicationDto> list = userApplication.applicationQuery();
		return list;
	}
}
