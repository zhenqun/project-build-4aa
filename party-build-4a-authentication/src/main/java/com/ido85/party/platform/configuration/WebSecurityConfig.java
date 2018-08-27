/**
 * 
 */
package com.ido85.party.platform.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;


/**
 * springsecurity配置
 * @author rongxj
 *
 */
@Configuration
//@EnableWebSecurity
//@Order(-20)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//	@Inject
//	private DataSource dataSource;
	
	
	/**
	 * sha256加密迭代次数
	 */
	@Value("${security.encrypt.hash-interations}")
	private int encrptInterations = 2;
	
	/**
	 * bcript加密迭代次数
	 */
	@Value("${security.encrypt.strength}")
	private int strength = 8;
	
	/**
	 * 使用的加密方式，默认是SHA-256
	 */
	@Value("${security.encrypt.encoder}")
	private String encoder = "SHA-256";
	
//	@Bean
//	@Override		
//	public AuthenticationManager authenticationManagerBean() throws Exception {
//		return super.authenticationManagerBean();
//	}
	
//	/**
//	 *  安全管理器，使用自定义的password匹配器
//	 */
//	@Override
//	public void configure(AuthenticationManagerBuilder auth) throws Exception {
//		ReflectionSaltSource saltSource = new ReflectionSaltSource();
//		saltSource.setUserPropertyToUse("salt");
//	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//	    provider.setPasswordEncoder(passwordEncoder());
//	    auth.authenticationProvider(provider);
//	}
	
	/**
	 * 密码加密匹配器
	 * @return
	 */
	@Bean public PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = null;
		if("bcrypt".equals(encoder)){
			encoder = new BCryptPasswordEncoder(strength);
		}else{
			encoder = new StandardPasswordEncoder();
		}
		return encoder;
	}
	
	/**
	 * http路径安全设置：登录方式、过滤器等
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/authentication/authentiInfoAysc");
	}
	
}
