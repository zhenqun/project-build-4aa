/**
 * 
 */
package com.ido85.party.sso.platform.configuration;

import com.ido85.party.sso.security.authentication.provisioning.PlateformUserDetailsManager;
import com.ido85.party.sso.security.oauth2.provider.PlatformClientDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.security.KeyPair;

/**
 * oauth2 server configuration
 * @author rongxj
 *
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter{

	@Inject
	private AuthenticationManager authenticationManager;
	
	@Inject
	private PlatformClientDetailService clientDetailService;
	
	@Inject
	private PlateformUserDetailsManager plateformUserDetailsManager;
	
	/**
	 * tokenServices添加刷新token支持
	 *  这里提供对oauth 2.0 默认的 token 处理
	 * @return
	 */
	@Bean
	@Primary
	public DefaultTokenServices tokenServices(){
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setTokenStore(tokenStore());
		return tokenServices;
	}

	@Inject
	private RandomValueAuthorizationCodeServices redisCodeServices;// 授权码服务
	/**
	 * 配置auth server 端点，增加自定义userDetailService
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//		endpoints.userApprovalHandler()
		endpoints
		.authenticationManager(this.authenticationManager)
		.userDetailsService(plateformUserDetailsManager)
//		.accessTokenConverter(jwtAccessTokenConverter());
		.tokenStore(tokenStore());
//		endpoints.authenticationManager(authenticationManager).accessTokenConverter(jwtAccessTokenConverter());
		//设置允许刷新token
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setTokenStore(tokenStore());
		tokenServices.setClientDetailsService(clientDetailService);
		tokenServices.setAuthenticationManager(authenticationManager);
		endpoints.tokenServices(tokenServices);
		endpoints.authorizationCodeServices(redisCodeServices);
	}
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer)
			throws Exception {
//		RoleHierarchyImpl
		oauthServer
			.tokenKeyAccess("permitAll()")
			.checkTokenAccess("isAuthenticated()")
			.accessDeniedHandler(new OAuth2AccessDeniedHandler())
			;
	}

	/**
	 * client接入配置，设置自定义clientDetailService  这里使用了自定义的客户端详情
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailService);
	}
	
	@Inject
	private DataSource dataSource;
	
	@Inject
	private RedisConnectionFactory connectionFactory;// 这里是springBoot 提供的redis 连接
		
//	@Bean
//	public JdbcTokenStore tokenStore() {
//		return new JdbcTokenStore(dataSource);
//	}
	
	/**
	 * 使用redis token store
	 * @return
	 */
	@Bean
	public TokenStore tokenStore(){
		return new RedisTokenStore(connectionFactory);//这里使用的是redis 的 tokenStore 1是性能比较好  2 是自动过期机制
		                                           // 符合token 的特点
	}
	
//	@Bean
//	public TokenStore tokenStore() {
//		return new JwtTokenStore(jwtAccessTokenConverter());
//	}
	
	@Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyPair keyPair = new KeyStoreKeyFactory(
                new ClassPathResource("keystore.jks"), "foobar".toCharArray())
                .getKeyPair("test");
        converter.setKeyPair(keyPair);
        return converter;
    }
	
//	@Bean
//    public JwtAccessTokenConverter jwtAccessTokenConverter() {
//        return new JwtAccessTokenConverter();
//    }

}
