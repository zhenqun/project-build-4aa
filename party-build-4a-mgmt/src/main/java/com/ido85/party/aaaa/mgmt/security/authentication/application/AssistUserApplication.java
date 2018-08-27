package com.ido85.party.aaaa.mgmt.security.authentication.application;

import com.ido85.party.aaaa.mgmt.business.dto.CheckBusinessUserParam;
import com.ido85.party.aaaa.mgmt.business.dto.ExportAuthorizationCodeParam;
import com.ido85.party.aaaa.mgmt.dto.AdminDto;
import com.ido85.party.aaaa.mgmt.dto.AdminQueryParam;
import com.ido85.party.aaaa.mgmt.dto.ApplicationAllotParam;
import com.ido85.party.aaaa.mgmt.dto.assist.*;
import com.ido85.party.aaaa.mgmt.dto.userinfo.AssistClientDto;
import com.ido85.party.aaaa.mgmt.dto.userinfo.ModifyPasswordParam;
import com.ido85.party.aaaa.mgmt.internet.dto.ApplicationDto;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/***
 *
 */
public interface AssistUserApplication {
	/**
	 * 获取该安全员能授权的应用系统
	 * @return
	 */
	public List<AssistClientDto> getAssistUserManage();


	/**
	 * 17地市县级
	 * @return
	 */
	public boolean getAssistIsModule();

	/**
	 * 根据查询条件查询业务管理员用户
	 * @param param
	 * @return
	 */
	public List<AssistUserDto> getAssistUserByCondition(AssistUserQueryDto param) throws Exception;

	/**
	 * 总数查询
	 * @param param
	 * @return
	 */
	public Long getAssistUserCntByCondition(AssistUserQueryDto param);


	/**
	 * 开通辅助安全员
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 */
	Map<String, String> addAssistSecurityUser(List<InAddAssistUserDto> param, HttpServletRequest request)throws Exception;

	/**
	 * 检测辅助安全员是否可用
	 * @param param
	 * @return
	 */
	public List<String> checkAssistUserExists(@Valid CheckBusinessUserParam param);

	/**
	 * 给辅助管理员授权
	 * @param param
	 * @return
	 */
	public Map<String, String> grantAssistUser(List<GrantAssistUserParam> param, HttpServletRequest request);

	/**
	 * 授权前查询拥有的权限
	 * @param fzuserId
	 * @return
	 */
	public List<GrantAssistClients> getAssistUserClientAndManage(AssistUserIdDto fzuserId);

	/**
	 * 撤销辅助安全员
	 * @param fzuserId
	 * @return
	 */
	public  Map<String,String> cancelAssistUserClient(AssistUserMultiDto fzuserId);

	/**
	 * 修改辅助安全员状态0 禁用 1启用)
	 * @param param
	 * @return
	 */
	public Map<String,String> modifyAssistUserStatus(UpdateAssistStateDto param,HttpServletRequest request);

	/**
	 * 审核校验
	 * @param param
	 * @return
	 */
	public List<CheckApplyUserResult> checkApplyUser(ApplyUserIdsDto param);
	/**
	 * 审核通过
	 * @param param
	 * @return
	 */
	public Map<String,String> passApplyUser(ApplyUserIdsDto param);

	/**
	 * 查询哪些人可以当辅助安全员
	 * @param users
	 * @return
	 */
	public List<String> checkAssistUserExists(@Valid List<CheckBusinessUserParam> users);


	/**
	 * 导出辅助安全员授权码
	 * @param param
	 * @param request
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	String exportAuthorizationCode(ExportAuthorizationCodeParam param, HttpServletRequest request) throws FileNotFoundException, IOException;

}
