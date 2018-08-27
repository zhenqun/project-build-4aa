package com.ido85.party.aaaa.mgmt.business.application;

import java.util.List;
import java.util.Map;

import com.ido85.party.aaaa.mgmt.dto.DataHandleDto;
import com.ido85.party.aaaa.mgmt.dto.assist.AssistRoleAddedQueryDto;
import com.ido85.party.aaaa.mgmt.dto.assist.AssistRoleModParam;
import com.ido85.party.aaaa.mgmt.dto.assist.AssistRoleQueryDto;
import com.ido85.party.aaaa.mgmt.dto.role.CategoryPermissionDto;
import com.ido85.party.aaaa.mgmt.dto.role.PermissionQueryDto;
import com.ido85.party.aaaa.mgmt.dto.role.RoleManageParam;
import com.ido85.party.aaaa.mgmt.dto.role.RolePermissionQueryDto;
import com.ido85.party.aaaa.mgmt.dto.role.RoleQueryDto;
import com.ido85.party.aaaa.mgmt.dto.role.RoleQueryParam;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.SecurityUserDto;

public interface RoleApplication {

	/**
	 * 新增 修改角色
	 * @param param
	 * @return
	 */
	Map<String, Object> roleManage(RoleManageParam param);

	List<RoleQueryDto> roleQuery(RoleQueryParam param);

	Long roleQueryCnt(RoleQueryParam param);

	/**
	 * 角色删除
	 * @param roleId
	 * @return
	 */
	boolean roleDel(String roleId);

	/**
	 * 查询该登录用户在该应用下的所有权限列表
	 * @return
	 */
	List<CategoryPermissionDto> permissionQuery(String clientId);
	
	/**
	 * 检测此权限是否可用
	 * @return
	 */
	boolean persimissionCheck(String clientId,String permissionManageId,String userManageId);

	/**
	 * 查询角色授权详情
	 * @return
	 */
	List<RolePermissionQueryDto> rolePermissionQuery(String roleId);


	List<DataHandleDto> getRoleUserById(String userId);

	/**
	 * 获取安全员能看到的所有角色(辅助安全员模块使用)
	 * @return
	 */
    List<AssistRoleQueryDto> assistRoleQuery();

	/**
	 * 角色包保存更新(辅助安全员模块使用)
	 * @param param
	 * @return
	 */
	Map<String,String> assistRoleMod(List<AssistRoleModParam> param);

	/**
	 * 获取保存过的角色包配置
	 * @return
	 */
	List<AssistRoleAddedQueryDto> assistRoleAddedQuery();
}
