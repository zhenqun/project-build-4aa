package com.ido85.party.sso.security.ldap.application;

public interface LdapApplication {

	/**
	 * 更新业务管理员vpn密码
	 * @param id
	 * @param vpnpwd
	 */
	boolean updateVpnPwd(String id, String vpnpwd);

}
