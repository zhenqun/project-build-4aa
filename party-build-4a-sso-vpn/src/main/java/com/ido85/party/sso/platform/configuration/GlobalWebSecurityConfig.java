/**
 * 
 */
package com.ido85.party.sso.platform.configuration;


import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.ido85.party.sso.security.authentication.provisioning.PlateformUserDetailsManager;
import com.ido85.party.sso.security.encoder.SHAPasswordEncoder;

/**
 * @author rongxj
 *
 */
//@SuppressWarnings("deprecation")
//@Configuration
//@EnableGlobalAuthentication
//@Order(-20)
public class GlobalWebSecurityConfig extends GlobalAuthenticationConfigurerAdapter {

//	@Inject
//	private DataSource dataSource;
	
	@Inject
	private PlateformUserDetailsManager userDetailsManager;
	
	@Value("${security.encrypt.hash-interations}")
	private int encrptInterations;
	

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		ReflectionSaltSource saltSource = new ReflectionSaltSource();
		saltSource.setUserPropertyToUse("salt");
	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	    provider.setSaltSource(saltSource);
	    provider.setUserDetailsService(userDetailsManager);
	    provider.setPasswordEncoder(passwordEncoder());
	    auth.authenticationProvider(provider);
	}
	
	
	/**
	 * rest 接入点
	 * @return
	 */
	@Bean(name="restAuthenticationEntryPoint")
	public AuthenticationEntryPoint restAuthenticationEntryPoint(){
		return new RestAuthenticationEntryPoint();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder(){
		SHAPasswordEncoder encoder = new SHAPasswordEncoder();
		encoder.setIterations(encrptInterations);
		return encoder;
	}
	
}
