/**
 * 
 */
package com.ido85.party.aaaa.mgmt.configuration;

import com.ido85.party.aaaa.mgmt.security.authentication.handler.LoginSuccessHandler;
import com.ido85.party.aaaa.mgmt.security.authentication.provider.DaoAuthenticationProvider;
import com.ido85.party.aaaa.mgmt.security.authentication.provisioning.PlateformUserDetailsManager;
import com.ido85.party.aaaa.mgmt.security.filters.DefaultUsernamePasswordAuthenticationFilter;
import com.ido85.party.aaaa.mgmt.security.filters.RestAuthenticationFilter;
import com.ido85.party.aaaa.mgmt.security.filters.SessionIsValidFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.authentication.encoding.PasswordEncoder;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

/**
 * @author rongxj
 *
 */
@Configuration
@EnableWebSecurity
// @Order(-20)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	// @Inject
	// private DataSource dataSource;

	@Inject
	private PlateformUserDetailsManager userDetailsManager;

	@Value("${security.encrypt.hash-interations}")
	private int encrptInterations = 2;

	@Value("${security.encrypt.strength}")
	private int strength = 8;

	@Value("${security.encrypt.encoder}")
	private String encoder = "SHA-256";

	@Inject 
	private LoginSuccessHandler loginSuccessHandler;
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public RestAuthenticationFilter restAuthenticationFilter() throws Exception {
		RestAuthenticationFilter restFilter = new RestAuthenticationFilter();
		restFilter.setAuthenticationManager(authenticationManagerBean());
		return restFilter;
	}

	@Override
	//TODO
	// 配置访问信息
	public void configure(WebSecurity web) throws Exception {
		 web.ignoring().antMatchers("/expand/checkHashAdmin","/expand/checkOrgAccount","/expand/checkOrgTypeAccount","/checkValidateCode/**", "/webjars/**", "/manage/jsTree/**", "/manage/css/**", "/css/**", "/manage/dist/**", "/manage/fonts/**","/manage/images/**","/manage/img/**",
				 "/manage/js/**", "/validateCodeServlet", "/internet/**", "/memberList/**", "/common/**", "/user/**", "/sendMessage/**", "/forget/**", "/mgmt/**","/expand/resetTelePhoneByAdmin", "/expand/checkAccount","/expand/delAccount","/account/saveAccount","/fresh/**");
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
//		ReflectionSaltSource saltSource = new ReflectionSaltSource();
//		saltSource.setUserPropertyToUse("salt");
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		// provider.setSaltSource(saltSource);
		provider.setUserDetailsService(userDetailsManager);
		provider.setPasswordEncoder(passwordEncoder());
		auth.authenticationProvider(provider);
		// auth.parentAuthenticationManager(authenticationManager);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/manage/css/**","/validateCodeServlet","/register")
				.permitAll().anyRequest().authenticated()
				.and().formLogin().loginPage("/login").defaultSuccessUrl("/")
				.permitAll().and().logout().logoutUrl("/logout").logoutSuccessUrl("/")
				.permitAll();

//		http.csrf().and().formLogin()
//				.loginPage("/login").defaultSuccessUrl("/worker-manage", true)
//				.usernameParameter("username").passwordParameter("password").permitAll()
//				.and().authorizeRequests().anyRequest().access("hasRole('ROLE_USER')").and().rememberMe()
//				.key("fjdnfoanfoinhonfeiwnofe");

		// http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
//		http.logout().invalidateHttpSession(true);
//		http.authorizeRequests().antMatchers("/validateCodeServlet").permitAll();
		http.addFilterBefore(usernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//		http.logout().invalidateHttpSession(true);
//		http.logout().permitAll();
//		http.logout().logoutSuccessHandler(logoutSuccessHandler());
	}

	/**
	 * rest 接入
	 * 
	 * @return
	 */
	@Bean(name = "restAuthenticationEntryPoint")
	public AuthenticationEntryPoint restAuthenticationEntryPoint() {
		return new RestAuthenticationEntryPoint();
	}

	// @Bean
	// public PasswordEncoder passwordEncoder(){
	// SHAPasswordEncoder encoder = new SHAPasswordEncoder();
	// encoder.setIterations(encrptInterations);
	// return encoder;
	// }

	@Bean
	PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = null;
		if ("bcrypt".equals(encoder)) {
			encoder = new BCryptPasswordEncoder(strength);
		} else {
			encoder = new StandardPasswordEncoder();
		}
		return encoder;
	}

	@Bean(name = "FORM_LOGIN_FILTER")
	public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() throws Exception {
		DefaultUsernamePasswordAuthenticationFilter filter = new DefaultUsernamePasswordAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManagerBean());
		filter.setAuthenticationSuccessHandler(loginSuccessHandler);
		return filter;
	}

	@Bean
	LogoutSuccessHandler logoutSuccessHandler() {
		SimpleUrlLogoutSuccessHandler logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
		logoutSuccessHandler.setUseReferer(false);
		logoutSuccessHandler.setTargetUrlParameter("redirect");
		return logoutSuccessHandler;
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
	
//    @Bean
//    public FilterRegistrationBean someFilterRegistration() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(sessionIsValidFilter());
//        registration.addUrlPatterns("/manage/**","/update/**");
//        registration.addInitParameter("paramName", "paramValue");
//        registration.setName("sessionFilter");
//        registration.setOrder(Integer.MAX_VALUE);
//        return registration;
//    }
//    
//	/**
//	 * @return
//	 */
//	@Bean
//	public SessionIsValidFilter sessionIsValidFilter(){
//		SessionIsValidFilter filter = new SessionIsValidFilter();
//		return filter;
//	}
}
