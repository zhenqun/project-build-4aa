package com.ido85.party.aaaa.mgmt.controller;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import com.ido85.party.aaaa.mgmt.dto.assist.AssistRoleAddedQueryDto;
import com.ido85.party.aaaa.mgmt.dto.assist.AssistRoleModParam;
import com.ido85.party.aaaa.mgmt.dto.assist.AssistRoleQueryDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ido85.party.aaaa.mgmt.business.application.RoleApplication;
import com.ido85.party.aaaa.mgmt.dto.base.OutBasePageDto;
import com.ido85.party.aaaa.mgmt.dto.role.CategoryPermissionDto;
import com.ido85.party.aaaa.mgmt.dto.role.PermissionQueryDto;
import com.ido85.party.aaaa.mgmt.dto.role.PermissionQueryParam;
import com.ido85.party.aaaa.mgmt.dto.role.RoleManageParam;
import com.ido85.party.aaaa.mgmt.dto.role.RolePermissionQueryDto;
import com.ido85.party.aaaa.mgmt.dto.role.RolePermissionQueryParam;
import com.ido85.party.aaaa.mgmt.dto.role.RoleQueryDto;
import com.ido85.party.aaaa.mgmt.dto.role.RoleQueryParam;

@RestController
public class RoleController {

	@Inject
	private RoleApplication roleApplication;
	
	/**
	 * 新增、修改角色
	 * @return
	 */
	@RequestMapping(path="/manage/business/roleManage",method={RequestMethod.POST})
	@ResponseBody
	@Transactional
	public Map<String,Object> roleManage(@Valid @RequestBody RoleManageParam param){
		Map<String,Object> result = roleApplication.roleManage(param);
		return result;
	}
	
	/**
	 * 查询当前应用下的角色
	 * @return
	 */
	@RequestMapping(path="/manage/business/roleQuery",method={RequestMethod.POST})
	@ResponseBody
	@Transactional
	public OutBasePageDto<RoleQueryDto> roleQuery(@Valid @RequestBody RoleQueryParam param){
		if(null == param.getPageNo()||param.getPageNo().intValue()<=0){
			param.setPageNo(1);
		}
		if( null == param.getPageSize()){
			param.setPageSize(20);
		}
		List<RoleQueryDto> roleList = roleApplication.roleQuery(param);
		Long cnt = roleApplication.roleQueryCnt(param);
		OutBasePageDto<RoleQueryDto> page = new OutBasePageDto<RoleQueryDto>();
		page.setCount(cnt);
		page.setData(roleList);
		page.setPageNo(param.getPageNo()); 
		page.setPageSize(param.getPageSize());
		return page;
	}
	
	/**
	 * 角色删除
	 * @return
	 */
	@RequestMapping(path="/manage/business/roleDel/{roleId}",method={RequestMethod.GET})
	@ResponseBody
	@Transactional
	public boolean roleDel(@PathVariable("roleId") String roleId){
		return roleApplication.roleDel(roleId);
	}
	
	/**
	 * 查询该登录用户在该应用下的所有权限列表
	 * @return
	 */
	@RequestMapping(path="/manage/business/permissionQuery",method={RequestMethod.POST})
	@ResponseBody
	@Transactional
	public List<CategoryPermissionDto> permissionQuery(@Valid @RequestBody PermissionQueryParam param){
		return roleApplication.permissionQuery(param.getClientId());
	}

	/**
	 * 查询角色授权详情
	 * @return
	 */
	@RequestMapping(path="/manage/business/rolePermissionQuery",method={RequestMethod.POST})
	@ResponseBody
	@Transactional
	public List<RolePermissionQueryDto> rolePermissionQuery(@Valid @RequestBody RolePermissionQueryParam param){
		return roleApplication.rolePermissionQuery(param.getRoleId());
	}

	/**
	 * 获取安全员能看到的所有角色(辅助安全员模块使用)
	 * @return
	 */
	@RequestMapping(path="/manage/business/assistRoleQuery",method={RequestMethod.POST})
	@ResponseBody
	public List<AssistRoleQueryDto> assistRoleQuery(){
		return roleApplication.assistRoleQuery();
	}

	/**
	 * 角色包保存更新(辅助安全员模块使用)
	 * @return
	 */
	@RequestMapping(path="/manage/business/assistRoleMod",method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> assistRoleMod(@Valid @RequestBody List<AssistRoleModParam> param){
		return roleApplication.assistRoleMod(param);
	}

	/**
	 * 获取保存过的角色包配置(辅助安全员模块使用)
	 * @return
	 */
	@RequestMapping(path="/manage/business/assistRoleAddedQuery",method={RequestMethod.POST})
	@ResponseBody
	public List<AssistRoleAddedQueryDto> assistRoleAddedQuery(){
		return roleApplication.assistRoleAddedQuery();
	}

}
