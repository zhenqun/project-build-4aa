/**
 * 
 */
package com.ido85.party.aaaa.mgmt.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * LDAP配置
 * @author rongxj
 *
 */
@Configuration
@EnableLdapRepositories(basePackages={"com.ido85.party"})
public class LDAPConfiguration {
	
	@Value("${ldap.url:ldap://127.0.0.1:389}")
	private String url;
	
	@Value("${ldap.login-dn:cn=Manager,dc=inspur,dc=com}")
	private String username;
	
	@Value("${ldap.password:password}")
	private String password;
	
	@Value("${ldap.base-dn:dc=inspur,dc=com}")
	private String baseDn;

	/**
	 * LDAP链接配置
	 * @return
	 */
	@Bean
	public ContextSource contextSource(){
		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setUrl(url);
		contextSource.setUserDn(username);
		contextSource.setPassword(password);
		contextSource.setBase(baseDn);
		return contextSource;
	}
	
	/**
	 * LDAP template
	 * @return
	 */
	@Bean
	public LdapTemplate ldapTemplate(){
		return new LdapTemplate(contextSource());
	}
}
