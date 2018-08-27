package com.ido85.party.sso.platform.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;


/**
 * oauth2资源服务配置
 * @author rongxj
 *
 */
@Configuration
@EnableResourceServer
public class ResouceServerConfiguration extends ResourceServerConfigurerAdapter {

	@Value("${maps.dtdjzx}")
	private String logoutSuccessUrl;
	
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
            .and()
                .csrf()
                .disable()
                .headers()
                .frameOptions().disable()
            .and()
                .authorizeRequests()
                .antMatchers("/swagger-ui/index.html").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
    }
    
    /**
     * 增加退出功能，在与authserver同一个应用中，webseciruty的logout功能不起作用，需要配置在这里
     * @return
     */
    @Bean
	LogoutSuccessHandler logoutSuccessHandler(){
		SimpleUrlLogoutSuccessHandler logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
		logoutSuccessHandler.setUseReferer(true);
		logoutSuccessHandler.setTargetUrlParameter("redir");
		logoutSuccessHandler.setDefaultTargetUrl(logoutSuccessUrl);
//		DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
//		logoutSuccessHandler.setRedirectStrategy(redirectStrategy);
		logoutSuccessHandler.setAlwaysUseDefaultTargetUrl(false);
		return logoutSuccessHandler;
	}
    
//    @Override
//	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
////    	resources.stateless(false);
////    	resources.resourceId("").stateless(false);
//    	super.configure(resources);
//	}
}
