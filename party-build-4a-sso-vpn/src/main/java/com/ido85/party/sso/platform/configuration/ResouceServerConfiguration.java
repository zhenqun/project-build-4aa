package com.ido85.party.sso.platform.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

/**
 * 资源服务配置
 * @author rongxj
 *
 */
@Configuration
@EnableResourceServer
public class ResouceServerConfiguration extends ResourceServerConfigurerAdapter {
	
    /**
     * 这里主要设置logout处理配置，在websecurity中配置不起作用，需要在此配置
     */
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
                .antMatchers("/swagger-ui/index.html","/").permitAll();// 访问这个地址 不用进行权限的认证
        http.authorizeRequests().anyRequest().authenticated();
    }
    
    /**
     * 登出处理器，使用头文件中referer中的uri跳转
     * @return
     */
    @Bean
	LogoutSuccessHandler logoutSuccessHandler(){
		SimpleUrlLogoutSuccessHandler logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
		logoutSuccessHandler.setUseReferer(true);
		logoutSuccessHandler.setTargetUrlParameter(null);
		return logoutSuccessHandler;
	}
}
