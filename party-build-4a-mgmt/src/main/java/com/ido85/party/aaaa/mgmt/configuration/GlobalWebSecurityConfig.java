/**
 * 
 */
package com.ido85.party.aaaa.mgmt.configuration;


import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.ido85.party.aaaa.mgmt.security.authentication.provisioning.PlateformUserDetailsManager;




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
	
	@Value("${security.encrypt.strength}")
	private int strength = 8;
	

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
	PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = null;
		if ("bcrypt".equals(encoder)) {
			encoder = new BCryptPasswordEncoder(strength);
		} else if ("SHA-256".equals(encoder)) {
			encoder = new StandardPasswordEncoder();
		}
		return encoder;
	}
	
}
