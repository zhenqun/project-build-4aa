/**
 * 
 */
package com.ido85.party.sso.platform.configuration;


import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.session.web.http.SessionRepositoryFilter;

import com.ido85.party.sso.filter.ClientRedirectFilter;
import com.ido85.party.sso.filter.PreAuthenticationFilter;
import com.ido85.party.sso.filter.PreloadingAuthenticationUserDetailsService;
import com.ido85.party.sso.handlers.LoginFailHandler;
import com.ido85.party.sso.handlers.LoginSuccessHandler;
import com.ido85.party.sso.security.authentication.provider.DaoAuthenticationProvider;
import com.ido85.party.sso.security.authentication.provisioning.PlateformUserDetailsManager;
import com.ido85.party.sso.security.filters.DefaultUsernamePasswordAuthenticationFilter;
//import com.ido85.party.sso.security.encoder.SHAPasswordEncoder;
import com.ido85.party.sso.security.filters.RestAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

/**
 * springsecurity配置
 * @author rongxj
 *
 */



@Configuration
@EnableWebSecurity
@Order(-20)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


//	@Inject
//	private DataSource dataSource;
	
	/**
	 * userdetails管理器，包含加载、创建、更新、更改密码等功能
	 */
	@Inject
	private PlateformUserDetailsManager userDetailsManager;
	
	
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
	
//	/**
//	 * 登出成功处理器
//	 */
//	@Inject
//	private LogoutSuccessHandler logoutSuccessHandler;
	
	@Inject 
	private LoginSuccessHandler loginSuccessHandler;
	
	@Inject
	private LoginFailHandler failureHandler;
	
	@Value("${maps.dtdjzx}")
	private String logoutSuccessUrl;

	@Value("${personalZoneUrl}")
	private String personalZoneUrl;

	@Bean
	@Override		
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public RestAuthenticationFilter restAuthenticationFilter() throws Exception{
		RestAuthenticationFilter restFilter = new RestAuthenticationFilter();
		restFilter.setAuthenticationManager(authenticationManagerBean());
		return restFilter;
	}
	
	/**
	 * 添加client过滤器 /oauth/authorize
	 * @return
	 */
	@Bean
	public ClientRedirectFilter clientRedirectFilter(){
		ClientRedirectFilter filter = new ClientRedirectFilter();
		return filter;
	}
	
	/**
	 * 添加client过滤器 /oauth/authorize
	 * @return
	 */
	@Bean
    public FilterRegistrationBean authorizeFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(clientRedirectFilter());
        registration.addUrlPatterns("/oauth/authorize");
        registration.setOrder(SessionRepositoryFilter.DEFAULT_ORDER + 1);
        return registration;
    }
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/webjars/**", "/css/**", "/fonts/**", "/validateCodeServlet", "/img/**","/checkValidateCode/**","/internet/**","/index", "/","/js/**",
				"/images/**","/register","/common/**","/success","/vpn/**","/retrievePassword","/sendMessage","/gettelephoneByusername","/unifiedLogout",
				"/verifyRetrievePwdVerificationCode","/retrievePasswordForWeb","/forget","/oauth/check_token","/cologin");
	}

	/**
	 *  安全管理器，使用自定义的password匹配器
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		ReflectionSaltSource saltSource = new ReflectionSaltSource();
		saltSource.setUserPropertyToUse("salt");
	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//	    provider.setSaltSource(saltSource);
	    provider.setUserDetailsService(userDetailsManager);
	    provider.setPasswordEncoder(passwordEncoder());
	    
//	    auth.parentAuthenticationManager(authenticationManager);
	    auth.authenticationProvider(preAuthenticationProvider());
	    auth.authenticationProvider(provider);

	}
	
	@Inject
	private PreloadingAuthenticationUserDetailsService authenticationUserDetailsService;
	
	/**
	 *	添加预加载机制，提供支持通过x-auth-token的方式进行登录 
	 */
	private AuthenticationProvider preAuthenticationProvider() {
	    PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
	    provider.setPreAuthenticatedUserDetailsService(authenticationUserDetailsService);

	    return provider;
	}
	
	/**
	 * http路径安全设置：登录方式、过滤器等
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		http.csrf().disable()
//			.exceptionHandling()
//        	.authenticationEntryPoint(restAuthenticationEntryPoint())
//		.and()
//		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
		http.csrf().disable();
//		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.formLogin()
//		http.formLogin()
//			.failureUrl("/loginForm?error")
			.loginPage("/login")
//			.loginProcessingUrl("/login")
			.defaultSuccessUrl(personalZoneUrl)
			.usernameParameter("username").passwordParameter("password")
//			.failureHandler(failureHandler)
			.permitAll()
		.and()
			.requestMatchers().antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access", "/api/login","/modify","/head-pic")
//			.requestMatchers().antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access", "/api/login", "/logout", "/oauth/token")
		.and()
			.authorizeRequests().anyRequest().authenticated()
		.and().rememberMe().key("fjdnfoanfoinhonfeiwnofe");
//		http.addFilterBefore(clientRedirectFilter(), BasicAuthenticationFilter.class);
		http.addFilterAfter(restAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
		http.logout().invalidateHttpSession(true);//.logoutSuccessHandler(new OAuthLogoutSuccessHandler());
		http.httpBasic().realmName("party");
//		OAuth2AuthenticationEntryPoint
		http.authorizeRequests().antMatchers("/validateCodeServlet").permitAll();
		http.addFilterBefore(usernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(headerAuthenticationFilter(), BasicAuthenticationFilter.class);
		http.logout().invalidateHttpSession(true);
		http.logout().permitAll();
		http.logout().logoutSuccessHandler(logoutSuccessHandler());
	}
	@Bean
	public PreAuthenticationFilter headerAuthenticationFilter() throws Exception {
	    return new PreAuthenticationFilter(authenticationManager());
	}
	
	/**
	 * rest 接入点
	 * @return
	 */
	@Bean(name="restAuthenticationEntryPoint")
	public AuthenticationEntryPoint restAuthenticationEntryPoint(){
		return new RestAuthenticationEntryPoint();
	}
	
//	@Bean
//	public PasswordEncoder passwordEncoder(){
//		SHAPasswordEncoder encoder = new SHAPasswordEncoder();
//		encoder.setIterations(encrptInterations);
//		return encoder;
//	}
	
	/**
	 * 密码加密匹配器
	 * @return
	 */
	@Bean PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = null;
		if("bcrypt".equals(encoder)){
			encoder = new BCryptPasswordEncoder(strength);
		}else{
			encoder = new StandardPasswordEncoder();
		}
		return encoder;
	}
	
	/**
	 * 自定义form登录方式过滤器
	 * @return
	 * @throws Exception
	 */
	@Bean(name="FORM_LOGIN_FILTER")
	public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() throws Exception{
		DefaultUsernamePasswordAuthenticationFilter filter = new DefaultUsernamePasswordAuthenticationFilter();
		loginSuccessHandler.setUseReferer(true);
		filter.setAuthenticationManager(authenticationManagerBean());
		filter.setAuthenticationSuccessHandler(loginSuccessHandler);
		filter.setAuthenticationFailureHandler(failureHandler);
		return filter;
	}
	
	@Bean
	SimpleUrlLogoutSuccessHandler logoutSuccessHandler(){
		SimpleUrlLogoutSuccessHandler logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
		logoutSuccessHandler.setUseReferer(true);
		logoutSuccessHandler.setTargetUrlParameter("redir");
		logoutSuccessHandler.setDefaultTargetUrl(logoutSuccessUrl);
//		DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
//		logoutSuccessHandler.setRedirectStrategy(redirectStrategy);
		logoutSuccessHandler.setAlwaysUseDefaultTargetUrl(false);
		return logoutSuccessHandler;
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
}
