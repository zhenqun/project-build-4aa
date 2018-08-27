package com.ido85.party.aaaa.mgmt.security.authentication.application;

import java.util.List;
import java.util.Map;

import com.ido85.party.aaaa.mgmt.business.domain.BusinessUser;
import com.ido85.party.aaaa.mgmt.dto.ResetVpnPassword;
import com.ido85.party.aaaa.mgmt.dto.expand.AccountSyncParam;
import com.ido85.party.aaaa.mgmt.dto.expand.CheckAccount;
import com.ido85.party.aaaa.mgmt.dto.expand.CheckOrgAccount;
import com.ido85.party.aaaa.mgmt.internet.dto.ApplicationDto;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;

public interface CommonApplication {

	/**
	 * 查询所有应用
	 * @return
	 */
	public List<ApplicationDto> applicationQuery();

	/**
	 * 校验手机验证码
	 * @param telephone
	 * @param register
	 * @return
	 */
	Map<String, Object> isVerifyCodeValid(String telephone, String type,String veifyCode);
	/**
	 * 互联网端校验手机验证码
	 * @param telephone
	 * @param register
	 * @return
	 */
	Map<String, Object> isSSOVerifyCodeValid(String telephone, String type,String veifyCode);

	/**
	 * // 生成正式的VPN账号并发送短信
	 * @param entities
	 */
	public boolean createVPN(String userId,String code,String phone,String idCard,String ou);

	//检测安全员审计员所选管理范围是否合法
	public boolean checkSecurityUserOu(String userId, String ou);

	/**
	 * 修改vpn密码
	 * @param vpn
	 * @param ouName
	 * @param vpnpwd
	 * @return
	 */
	public boolean updateVpnPwd(String vpn, String ouName, String vpnpwd);

	/**
	 * 创建安全员审计员vpn
	 * @param userId
	 * @param code
	 * @param telephone
	 * @param idCard
	 * @param ouName
	 * @return
	 */
	public boolean createVPNForSecurity(String userId, String code, String telephone, String idCard, String ouName,String type);

	/**
	 * 检测业务管理员节点是否合法
	 * @param userId
	 * @param ouName
     * @return
     */
	boolean checkBusinessOu(String userId, String ouName);

	/**
	 * 判断用户是否为业务管理员
	 * @param hash
	 * @return
     */
	boolean checkIsBusinessUser(String hash);

	/**
	 * 判断用户是否为安全员或者审计员
	 * @param hash
	 * @return
     */
	boolean checkIsSecOrAudi(String hash);

	/**
	 * 重置vpn密码
	 * @param param
	 * @return
     */
	boolean resetVpnPassword(ResetVpnPassword param);

	/**
	 * 查询组织下是否存在账号
	 * @param param
	 * @return
     */
	boolean checkOrgAccount(CheckOrgAccount param);

	/**
	 * 账号同步  内容管理系统使用
	 */
	Map<String,String> accountSync(AccountSyncParam param);

	/**
	 * 查询hashs下是否为三员
	 * @param hashs
	 * @return
	 */
	List<String> checkHashAdmin(List<String> hashs);

	/**
	 * 检测该账号是否可以进行删除（党员信息整改）
	 * @param checkAccount
	 * @return
	 */
    Map<String,String> checkAccount(CheckAccount checkAccount);

	/**
	 * 删除账号（党员信息整改）
	 * @param checkAccount
	 * @return
	 */
	public boolean delAccount(CheckAccount checkAccount);
	/**
	 * 查询组织下是否存在账号----修改党组织类别使用
	 * @param param
	 * @return
	 */
	boolean checkOrgTypeAccount(CheckOrgAccount param);

}
