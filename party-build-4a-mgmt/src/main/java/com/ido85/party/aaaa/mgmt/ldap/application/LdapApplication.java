package com.ido85.party.aaaa.mgmt.ldap.application;

import com.ido85.party.aaaa.mgmt.business.domain.BusinessUserVpnRel;
import com.ido85.party.aaaa.mgmt.dto.expand.ChangeVPNBatchDto;
import com.ido85.party.aaaa.mgmt.dto.ldap.AddAccountDto;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserVpnRel;

import java.util.List;

public interface LdapApplication {

	/**
	 * 创建VPN
	 * @param dto
	 * @return
	 */
	boolean createVPN(AddAccountDto dto);

	/**
	 * 修改vpn密码
	 * @param vpn
	 * @param ouName
	 * @param vpnpwd
	 * @return
	 */
	boolean updateVpnPwd(String vpn, String ouName, String vpnpwd);

	/**
	 * 批量删除安全员审计员vpn账户
	 * @param rels
	 * @return
     */
	boolean deleteBatch(List<UserVpnRel> rels);

	/**
	 * 批量删除业务管理员vpn账户
	 * @param vpns
	 * @return
     */
	boolean deleteBusinessVpnBatch(List<BusinessUserVpnRel> vpns);

	/**
	 * 重置vpn密码
	 * @param ou
	 * @param uid
     * @return
     */
	boolean resetVpnPassword(String ou, String uid);

	/**
	 * 更换vpn账号节点
	 * @param ouName
	 * @param vpn
     * @param ou
     */
	void changeVPNOu(String ouName, String vpn, String ou,String userId);

	/**
	 * 批量更换vpn账号节点
	 * @param changeVPNBatchDtos
	 */
	void changeVPNOuBatch(List<ChangeVPNBatchDto> changeVPNBatchDtos);

	/**
	 * 更换vpn账号节点
	 * 原方法changeVPNOu会导致账号密码无效，特采用这个方法
	 * @param ouName
	 * @param vpn
	 * @param ou
	 */
	void changeVPNOuPwd(String ouName, String vpn, String ou, String userId);
}
