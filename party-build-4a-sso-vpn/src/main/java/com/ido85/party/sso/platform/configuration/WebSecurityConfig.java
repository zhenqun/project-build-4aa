/**
 * 
 */
package com.ido85.party.sso.platform.configuration;


import com.ido85.party.sso.handlers.LoginFailHandler;
import com.ido85.party.sso.handlers.LoginSuccessHandler;
import com.ido85.party.sso.security.authentication.provider.DaoAuthenticationProvider;
import com.ido85.party.sso.security.authentication.provider.DefaultDaoAuthenticationProvider;
import com.ido85.party.sso.security.authentication.provisioning.PlateformUserDetailsManager;
import com.ido85.party.sso.security.encoder.DefaultPasswordEncoder;
import com.ido85.party.sso.security.filters.ClientRedirectFilter;
import com.ido85.party.sso.security.filters.DefaultUsernamePasswordAuthenticationFilter;
import com.ido85.party.sso.security.filters.RestAuthenticationFilter;
import com.ido85.party.sso.security.oauth2.provider.PreloadingAuthenticationUserDetailsService;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.authentication.encoding.PasswordEncoder;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import com.ido85.party.sso.security.encoder.SHAPasswordEncoder;

/**
 * web安全配置
 * @author rongxj
 *
 */
@Configuration
@EnableWebSecurity
@Order(-20)  // 这里是指明多个 WebSecurityConfigurerAdapter 继承类的优先级 如果不指定的话 默认为1 最后被执行
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//	@Inject
//	private DataSource dataSource;
	
	@Inject
	private PlateformUserDetailsManager userDetailsManager;
	
	
	@Value("${security.encrypt.hash-interations}")
	private int encrptInterations = 2;
	
	@Value("${security.encrypt.strength}")
	private int strength = 8;
	
	@Value("${security.encrypt.encoder}")
	private String encoder = "SHA-256";
	
	@Inject
	private LogoutSuccessHandler logoutSuccessHandler;//  com.ido85.party.sso.handlers; 这里是自定义的logoutSucessHandler
	
	@Inject 
	private LoginSuccessHandler loginSuccessHandler;  // 登录成功后 做些日志记录的信息
	
	@Inject
	private LoginFailHandler failureHandler;            // 登录失败后 做一些日志记录的信息
	
	@Bean
	@Override		
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean(); //认证管理
	}

	/**
	 *  这里对验证 方法等做了一个初步的过滤
	 * @return
	 * @throws Exception
	 */
	@Bean
	public RestAuthenticationFilter restAuthenticationFilter() throws Exception{
		RestAuthenticationFilter restFilter = new RestAuthenticationFilter();
		restFilter.setAuthenticationManager(authenticationManagerBean());
		return restFilter;
	}
	
	/**
	 * 添加client过滤器 /oauth/authorize
	 * @return
	 *
	 *   这里配置了客户端的过滤器
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
	// pring安全在Web层（UI和HTTP后台）是基于Servlet过滤器实现的.
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/fonts/**","/webjars/**", "/css/**", "/validateCodeServlet", "/checkValidateCode/**","/acc/**", "/index", "/", "/img/**","/images/**","/js/**", "/register","/common/**","/success","/vpn/**","/sendMessage","/gettelephoneByusername","/verifyRetrievePwdVerificationCode","/retrievePasswordForWeb","/forget/**","/oauth/check_token","/application-sdk","/notice/**");
		// 这里主要是用来忽略这些访问静态资源路径的验证
	}


	/**
	 *    安全管理器，使用自定义的password匹配器
	 *    authorization  授权     authentication  认证
	 *    这里是基础的网站安全java配置
	 *    AuthenticationManagerBuilder  管理所有的安全认证（ 包括 inMemoryAuthentication 和 jdbcAuthentication）的一个类
	 *
	 *     AuthenticationManager, ProviderManager(AuthenticationManager的一个实现类，并将
	 *     工作委派给 AuthenticationProvider 列表，然后依次执行列表，判断是否需要其进行验证) 与AuthenticationProvider
	 *     验证一个请求的话   userDetails和UserDetailServiece 是两种方式 而 userDetails 是最常用的方式  uthenticationProvider 就是采用的这种方式
	 *     被加载的UserDetails 对象(包含了GrantedAuthority s),在认证成功后，将会被用于填充的Authentication 对象，并且存储在SecurityContext中。
	 *
	 *
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		ReflectionSaltSource saltSource = new ReflectionSaltSource();
		saltSource.setUserPropertyToUse("salt");

	    DaoAuthenticationProvider provider2 = new DaoAuthenticationProvider();// 这里是认证提供
	    DefaultDaoAuthenticationProvider provider = new DefaultDaoAuthenticationProvider();
//	    provider.setSaltSource(saltSource);
	    provider.setUserDetailsService(userDetailsManager);// 这里是采用的UserServiceDetaisl 这种方式实现的
	    provider.setPasswordEncoder(passwordEncoder());// 下面定义了一个密码加密匹配器
	    provider2.setUserDetailsService(userDetailsManager);  // 这里  userDetailsManager 实现了 Userservicedetails 类
	    provider2.setPasswordEncoder(passwordEncoder());    // passwordEncoder() 自定义的密码加密器
	    auth.authenticationProvider(provider);
	    auth.authenticationProvider(provider2);
		auth.authenticationProvider(preAuthenticationProvider());// 这里提供了 oauth 2.0 的认证

//	    auth.parentAuthenticationManager(authenticationManager);
	}

	@Inject
	private PreloadingAuthenticationUserDetailsService authenticationUserDetailsService;// 这里是oauth 2.0 的东西

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
		http.csrf().disable(); // 禁用csrf 防护  这里默认是开启的。

//			.exceptionHandling()
//        	.authenticationEntryPoint(restAuthenticationEntryPoint())
//		.and()
//		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
		http.formLogin()//启用form 表单验证
//		http.formLogin()
//			.failureUrl("/loginForm?error")
			.loginPage("/login")// 配置登录页面
//			.loginProcessingUrl("/login")
//			.defaultSuccessUrl("/success", true)
			.usernameParameter("username").passwordParameter("password")// 配置验证参数
			.permitAll()// 允许所有用户的基于表单登录的url进行访问。
		.and()
			.requestMatchers().//http.authorizeRequests()方法有多个子节点，每个macher按照他们的声明顺序执行。
				antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access", "/api/login","/modify","/head-pic","/modify-vpn","/modify-phone")// 只验证一这些url开头的连接上
//			.requestMatchers().antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access", "/api/login", "/logout", "/oauth/token")
		.and()
			.authorizeRequests().anyRequest().authenticated()
		.and().rememberMe().key("fjdnfoanfoinhonfeiwnofe");
		
		http.addFilterAfter(restAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

		/**  进行登出操作功能 默认WebSecurityConfigerAdapter是默认开启的 而且内置了跳转地址 **/
		http.logout().invalidateHttpSession(true);//.logoutSuccessHandler(new OAuthLogoutSuccessHandler());// 退出系统后 清楚Httpsession 默认是开启的
		http.httpBasic().realmName("party");
//		OAuth2AuthenticationEntryPoint
		http.authorizeRequests().antMatchers("/validateCodeServlet").permitAll();// 所有的访问该url 的用户都被允许
		http.addFilterBefore(usernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		http.logout().invalidateHttpSession(true);// 登出时候 让session  无效
		http.logout().permitAll();               // 允许所有的登出请求
		http.logout().logoutSuccessHandler(logoutSuccessHandler).logoutSuccessUrl("/index");// 登录成功后跳转的地址
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
			encoder = new DefaultPasswordEncoder();
		}
		return encoder;
	}
	
	/**
	 * 自定义form登录方式过滤器
	 * 这里自定义了一个form 表单提交验证的过滤器
	 * @return
	 * @throws Exception
	 */
	@Bean(name="FORM_LOGIN_FILTER")
	public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() throws Exception{
		DefaultUsernamePasswordAuthenticationFilter filter = new DefaultUsernamePasswordAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManagerBean());
		filter.setAuthenticationSuccessHandler(loginSuccessHandler);
		filter.setAuthenticationFailureHandler(failureHandler);
		return filter;
	}
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
	
}
