package com.ido85.party.aaaa.mgmt.security.authentication.application;

import com.ido85.party.aaaa.mgmt.business.dto.ExportAuthorizationCodeParam;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserClientRel;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by fire on 2017/2/22.
 */
public interface SecurityUserApplication {
	/**
	 * 开通安全管理员
	 * 要进行实名认证
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String, String> addSecurityUser(List<InAddSecurityUserDto> param,HttpServletRequest request)throws Exception;

	/**
	 * 查询下级安全员
	 * @param param
	 * @return
	 */
	List<SecurityUserDto> securityUserQuery(SecurityUserQueryParam param);

	/**
	 * 下级安全员数量查询
	 * @param param
	 * @return
	 */
	Long securityUserQueryCnt(SecurityUserQueryParam param);

	/**
	 * 修改安全员状态
	 * @param param
	 * @return
	 */
	Map<String, String> modifySecurityUserState(ModifySecurityUserStatusParam param,HttpServletRequest request);

	/**
	 * 注销安全员
	 * @param param
	 * @return
	 */
	Map<String, String> cancellationSecurityUser(CancellationSecurityUserParam param,HttpServletRequest request);

	/**
	 * 为安全员设置管理范围
	 * @param param
	 * @return
	 */
	Map<String, String> setManageScope(List<SetSecurityUserManageScopeParam> param,HttpServletRequest request);

	/**
	 * 导出授权码
	 * @param param
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	String exportAuthorizationCode(ExportAuthorizationCodeParam param,HttpServletRequest request) throws FileNotFoundException, IOException;

	/**
	 * 取消安全员应用管理
	 * @param param
	 * @return
     */
	boolean cancelSecUserClient(CancelSecUserClientParam param);

	/**
	 * 检测撤销用户是否存在会删除账号的情况
	 */
	List<String> checkCancelSecUser(CancelSecUserClientParam param);
	/**
	 * 整建制删除安全员所有账号信息
	 * @param param
	 * @return
	 */
	boolean delSecUserClient(List<String> userId);


	List<UserClientRel> getRelByManageId(String orgId);


	/**
	 * 党组织改名
	 *
	 * @return
	 */
	int reNameOrgName(String newOrgName, String orgId);


	/**
	 * 辅助安全员权限模块改名
	 * @param newOrgName
	 * @param orgId
	 * @return
	 */
	int reNameAssistOrgName(String newOrgName, String orgId);


}
