package com.ido85.party.aaaa.mgmt.ldap.application.impl;

import com.ido85.party.aaaa.mgmt.business.domain.BusinessUserVpnRel;
import com.ido85.party.aaaa.mgmt.business.resources.BusinessuserVpnRelResources;
import com.ido85.party.aaaa.mgmt.dto.expand.ChangeVPNBatchDto;
import com.ido85.party.aaaa.mgmt.dto.ldap.AddAccountDto;
import com.ido85.party.aaaa.mgmt.ldap.application.LdapApplication;
import com.ido85.party.aaaa.mgmt.ldap.crypt.SHA512Crypt;
import com.ido85.party.aaaa.mgmt.ldap.domain.Account;
import com.ido85.party.aaaa.mgmt.ldap.domain.AccountPwdChange;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserVpnRel;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserVpnRelResources;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import com.ido85.party.aaaa.mgmt.service.SyncService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.NameAlreadyBoundException;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
@Named
public class LdapApplicationImpl implements LdapApplication {

	@Inject
	private SyncService service;
	
	@Value("${ldap.base-dn}")
	private String baseDn;

	@Inject
	private UserVpnRelResources userVpnRelResources;

	@Inject
	private BusinessuserVpnRelResources businessuserVpnRelResources;
	
	@Override
	public boolean createVPN(AddAccountDto dto) {
		Account account = null;
		String ou = dto.getOu();
		String password = dto.getPassword();
		String idCard = dto.getIdCard();
		if(null != ou){
			//创建ldap 对象
			account = new Account();
			account.setGidNumber(1000);
			account.setHomeDirectory("/home/"+idCard);
			account.setMail(null);
			account.setName(idCard);
			account.setOu(ou);
			account.setSurname(idCard);
			account.setUid(idCard);
			account.setUidNumber(10001);
			if(StringUtils.isNull(password)){
				account.setUserPassword(SHA512Crypt.sha512Crypt(idCard.substring(idCard.length()-6, idCard.length()),
						idCard.substring(idCard.length()-6, idCard.length())));
			}else{
				account.setUserPassword(SHA512Crypt.sha512Crypt(password,
						idCard.substring(idCard.length()-6, idCard.length())));
			}
//					account.setDn(service.buildDn(account.getUid(), account.getOu()));
			account.setShadowLastChange(17277);
			account.setShadowMax(365);
			account.setShadowMin(5);
			account.setShadowWarning(30);
			try {
				service.save(account);
			} catch (NameAlreadyBoundException e) {
				//如果已经存在则为已经成功
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean updateVpnPwd(String vpn, String ouName, String vpnpwd) {
		Account at = service.getAccount(vpn, ouName);
		if(null == at){
			return false;
		}else{
//			String pwd = at.getUserPassword();
//			if(!SHA512Crypt.matches(oldPwd, pwd)){
//				map.put("flag", "fail");
//				map.put("message", "原密码错误");
//				return map;
//			}else{
				at.setUserPassword(SHA512Crypt.sha512Crypt(vpnpwd,
						vpn.substring(vpn.length()-3, vpn.length())));
				service.save(at);
				return true;
//			}
		}
	}

	/**
	 * 批量删除vpn账户
	 * @param rels
	 * @return
     */
	public boolean deleteBatch(List<UserVpnRel> rels) {
		//查出所有account
		Account ac = null;
		List<Account> acList = null;
		if(ListUntils.isNotNull(rels)){
			acList = new ArrayList<>();
			for(UserVpnRel rel:rels){
				ac = service.getAccount(rel.getVpn(),rel.getOuName());
				if(null != ac){
					acList.add(ac);
				}
			}
			if(ListUntils.isNotNull(acList)){
				service.deleteAll(acList);
			}
		}
		//删除所有account
		return true;
	}

	/**
	 * 批量删除业务管理员vpn账户
	 * @param vpns
	 * @return
     */
	public boolean deleteBusinessVpnBatch(List<BusinessUserVpnRel> vpns) {
		Account ac = null;
		List<Account> acList = null;
		if(ListUntils.isNotNull(vpns)){
			acList = new ArrayList<>();
			for(BusinessUserVpnRel rel:vpns){
				ac = service.getAccount(rel.getVpn(),rel.getOu());
				if(null != ac){
					acList.add(ac);
				}
			}
			service.deleteAll(acList);
		}
		return true;
	}

	/**
	 * 重置vpn密码
	 * @param ou
	 * @param uid
     * @return
     */
	public boolean resetVpnPassword(String ou, String uid) {
		Account at = service.getAccount(uid, ou);
		if(null == at){
			return false;
		}else{
			at.setUserPassword(SHA512Crypt.sha512Crypt(uid.substring(uid.length()-6, uid.length()),
					uid.substring(uid.length()-6, uid.length())));
			service.save(at);
			return true;
		}
	}

	/**
	 * 更换vpn账号节点
	 * @param ouName
	 * @param vpn
     * @param ou
     */
	public void changeVPNOu(String ouName, String vpn, String ou,String userId) {
		Account at = service.getAccount(vpn,ouName);
		if(null != at){
			Account account = new Account();
			account.setGidNumber(1000);
			account.setHomeDirectory("/home/"+at.getUid());
			account.setMail(null);
			account.setName(at.getUid());
			account.setOu(ou);
			account.setSurname(at.getUid());
			account.setUid(at.getUid());
			account.setUidNumber(10001);
			account.setUserPassword(at.getUserPassword());
			account.setShadowLastChange(17277);
			account.setShadowMax(365);
			account.setShadowMin(5);
			account.setShadowWarning(30);
			service.delete(at);
			service.save(account);
			BusinessUserVpnRel bl = businessuserVpnRelResources.getRelByUserId(userId);
			if(null != bl){
				bl.setOu(ou);
				businessuserVpnRelResources.save(bl);
			}
		}else{
			Account account = new Account();
			account.setGidNumber(1000);
			account.setHomeDirectory("/home/"+vpn);
			account.setMail(null);
			account.setName(vpn);
			account.setOu(ou);
			account.setSurname(vpn);
			account.setUid(vpn);
			account.setUidNumber(10001);
			account.setUserPassword(SHA512Crypt.sha512Crypt(vpn.substring(vpn.length()-6, vpn.length()),
					vpn.substring(vpn.length()-6, vpn.length())));
			account.setShadowLastChange(17277);
			account.setShadowMax(365);
			account.setShadowMin(5);
			account.setShadowWarning(30);
			service.save(account);
		}
	}

	@Override
	public void changeVPNOuBatch(List<ChangeVPNBatchDto> changeVPNBatchDtos) {
		if(ListUntils.isNotNull(changeVPNBatchDtos)){
			for(ChangeVPNBatchDto changeVPNBatchDto:changeVPNBatchDtos){
				AccountPwdChange at = service.getAccountPwdChange(changeVPNBatchDto.getVpn(),changeVPNBatchDto.getOldOu());
				if(null != at){
					at.setOu(changeVPNBatchDto.getOu());
					service.saveAccountPwdChange(at);
				}
			}
		}
	}

	@Override
	public void changeVPNOuPwd(String ouName, String vpn, String ou, String userId) {
		AccountPwdChange at = service.getAccountPwdChange(vpn,ouName);
		if(null != at){
			at.setOu(ou);
			service.saveAccountPwdChange(at);
			BusinessUserVpnRel bl = businessuserVpnRelResources.getRelByUserId(userId);
			if(null != bl){
				bl.setOu(ou);
				businessuserVpnRelResources.save(bl);
			}
		}else{
			Account account = new Account();
			account.setGidNumber(1000);
			account.setHomeDirectory("/home/"+vpn);
			account.setMail(null);
			account.setName(vpn);
			account.setOu(ou);
			account.setSurname(vpn);
			account.setUid(vpn);
			account.setUidNumber(10001);
			account.setUserPassword(SHA512Crypt.sha512Crypt(vpn.substring(vpn.length()-6, vpn.length()),
					vpn.substring(vpn.length()-6, vpn.length())));
			account.setShadowLastChange(17277);
			account.setShadowMax(365);
			account.setShadowMin(5);
			account.setShadowWarning(30);
			service.save(account);
		}
	}

}
