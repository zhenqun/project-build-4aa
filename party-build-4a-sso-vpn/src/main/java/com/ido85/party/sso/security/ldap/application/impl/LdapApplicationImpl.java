package com.ido85.party.sso.security.ldap.application.impl;

import com.ido85.party.sso.security.authentication.domain.UserVpnRel;
import com.ido85.party.sso.security.authentication.repository.UserVpnResources;
import com.ido85.party.sso.security.ldap.application.LdapApplication;
import com.ido85.party.sso.security.ldap.application.crypt.SHA512Crypt;
import com.ido85.party.sso.security.ldap.application.domain.Account;
import com.ido85.party.sso.security.ldap.application.resources.AccountRepository;
import com.ido85.party.sso.security.ldap.application.service.SyncService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;

@Named
@Slf4j
public class LdapApplicationImpl implements LdapApplication {

	@Inject
	private UserVpnResources userVpnResources;
	
	@Inject
	private AccountRepository accountResouces;
	
	@Inject
	private SyncService service;
	
	/**
	 * 更新vpn密码
	 */
	public boolean updateVpnPwd(String id, String vpnpwd) {
		String ou = null;
		String vpn = null;
		//查询用户vpn以及节点
		UserVpnRel rel = userVpnResources.getRelById(id);
		if(null != rel){
			ou = rel.getOu();
			vpn = rel.getVpn();
			log.info("---------激活修改vpn密码-----------userId:"+id+", ou:"+ou+" ,vpn:"+vpn);
			Account at = service.getAccount(vpn, ou);
			if(null == at){
				log.info("----------激活修改vpn密码--------未找到关联关系");
				return false;
			}else{
				String pwd = SHA512Crypt.sha512Crypt(vpnpwd,
						vpn.substring(vpn.length()-3, vpn.length()));
				at.setUserPassword(pwd);
				log.info("----------开始修改vpn密码--------准备修改为:"+vpnpwd);
				Account account = service.save(at);
				if(null != account){
					log.info("----------激活修改vpn密码--------修改成功--------,修改后密码为:"+account.getUserPassword());
				}
				return true;
			}
		}
		return false;
	}

}
