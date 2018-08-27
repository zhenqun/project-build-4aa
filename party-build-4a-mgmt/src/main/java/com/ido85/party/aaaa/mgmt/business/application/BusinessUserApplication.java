package com.ido85.party.aaaa.mgmt.business.application;

import com.ido85.party.aaaa.mgmt.business.domain.BusinessUser;
import com.ido85.party.aaaa.mgmt.business.dto.*;
import com.ido85.party.aaaa.mgmt.dto.BusinessUserRoleDto;
import com.ido85.party.aaaa.mgmt.dto.BusinessUserRoleQueryParam;
import com.ido85.party.aaaa.mgmt.dto.CheckRoleParam;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/***
 * 网上 会员用户
 *
 */
public interface BusinessUserApplication {
	
	/**
	 * 根据查询条件查询业务管理员用户
	 * @param param
	 * @return
	 */
	public List<BusinessUserDto> getBusinessUserByCondition(BusinessUserQueryParam param);

	/**
	 * 业务管理员查询条数
	 * @param param
	 * @return
	 */
	public Long getBusinessUserCntByCondition(BusinessUserQueryParam param);
	
	/**
	 * 根据用户id获取businessUser
	 * @param userId
	 * @return
	 */
	public BusinessUser getBusinessUserById(String userId);

	/**
	 * 保存businessUser
	 * @param user
	 */
	public void saveBusinessUser(BusinessUser user);

	/**
	 * 开通业务员
	 * @param param
	 * @return
	 */
	public Map<String, String> addBusinessUser(List<AddBusinessUser> param,HttpServletRequest request);

	/**
	 * 业务管理员状态修改
	 * @param param
	 * @return
	 */
	public Map<String, String> modifyBusinessUserStatus(ModifyBusinessUserStatusParam param,HttpServletRequest request);

	/**
	 * 注销业务管理员
	 * @param param
	 * @return
	 */
	public Map<String, String> cancellationBusinessUser(CancellationBusinessUserParam param,HttpServletRequest request);

	/**
	 * 为业务管理员设置管理范围（批量）
	 * @param param
	 * @return
	 */
	public Map<String, String> setManageScope(SetManageScopeParam param,HttpServletRequest request);

	/**
	 * 给业务管理员授权
	 * @param param
	 * @return
	 */
	public Map<String, String> grantBusinessUser(List<GrantBusinessUserParam> param,HttpServletRequest request);

	/**
	 * 导出业务管理员授权码
	 * @param param
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public String exportAuthorizationCode(ExportAuthorizationCodeParam param,HttpServletRequest request) throws FileNotFoundException, IOException;

	/**
	 * 根据用户id查询应用角色详情
	 * @param businessUserId
	 * @param clientId
	 * @return
	 */
	public List<ClientRolesDto> clientRoleQuery(String businessUserId,String clientId);

	/**
	 * 业务管理员角色查询
	 * @param param
	 * @return
	 */
	public List<BusinessUserRoleDto> businessUserRoleQuery(BusinessUserRoleQueryParam param);

	/**
	 * 检测角色授权是否合法
	 * @param param
	 * @return
	 */
	public boolean checkRole(CheckRoleParam param);

	/**
	 * 检测撤销用户在改应用下的管理范围
	 */
	List<String> checkcancelBusiUserClient(CancelBusiUserClientParam param);

	/**
	 * 撤销用户在改应用下的管理范围
	 * @param param
	 * @return
     */
	boolean cancelBusiUserClient(CancelBusiUserClientParam param);

	/**
	 * 根据hash获取用户
	 * @param hash
	 * @return
     */
	BusinessUser getBusinessUserByHash(String hash);

	/**
	 * 根据hash查询管理员
	 * @param hashs
	 * @return
	 */
    List<BusinessUser> checkHashAdmin(List<String> hashs);


	/**
	 * vpn端党组织定时改名
	 * @param newOrgName
	 * @param orgId
	 * @return
	 */
	int reNameOrgName(String newOrgName,String orgId);
}
