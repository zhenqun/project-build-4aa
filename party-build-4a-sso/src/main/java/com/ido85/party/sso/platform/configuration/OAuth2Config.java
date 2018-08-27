/**
 * 
 */
package com.ido85.party.sso.platform.configuration;

import com.ido85.party.sso.platform.security.oauth2.token.store.RedisTokenStore;
import com.ido85.party.sso.platform.security.oauth2.token.store.RedisTokenStoreSingle;
import com.ido85.party.sso.security.authentication.provisioning.PlateformUserDetailsManager;
import com.ido85.party.sso.security.oauth2.provider.PlatformClientDetailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
//import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.inject.Inject;
import java.security.KeyPair;

/**
 * 
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

	@Inject
	private RandomValueAuthorizationCodeServices redisCodeServices;

	@Inject
	private RedisTemplate redisTemplate;

	@Value("${spring.redis.cluster.nodes:}")
	private String redisClusterNodes;

	/**
	 * tokenServices添加刷新token支持
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
	
	/**
	 * 配置oauth2端点
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
	 * client接入配置，设置自定义clientDetailService
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailService);
//		clients.inMemory()
//			.withClient("party-education-ui")
//			.secret("party-education-ui-secret")
//			.authorities("ROLE_TRUSTED_CLIENT").resourceIds("party-education-server")
//			.authorizedGrantTypes("authorization_code", "refresh_token", "password", "implicit","client_credentials")
//			.scopes("ui1.read", "openid")
//			.autoApprove(true)
//		.and()
//			.withClient("ui2")
//			.secret("ui2-secret")
//			.authorities("ROLE_TRUSTED_CLIENT")
//			.authorizedGrantTypes("authorization_code", "refresh_token", "password")
//			.scopes("ui2.read", "ui2.write","openid")
//			.autoApprove(true)
//		.and()
//			.withClient("mobile-app")
//			.authorities("ROLE_CLIENT")
//			.authorizedGrantTypes("implicit", "refresh_token")
//			.scopes("read")
//			.autoApprove(true)
//		.and()
//			.withClient("customer-integration-system")	
//			.secret("1234567890")
//			.authorities("ROLE_CLIENT")
//			.authorizedGrantTypes("client_credentials")
//			.scopes("read")
//			.autoApprove(true).accessTokenValiditySeconds(3600);
	}
	
	@Inject
	private RedisConnectionFactory connectionFactory;
		
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
		if(redisClusterNodes == null || "".equals(redisClusterNodes.trim())){
			return new RedisTokenStoreSingle(connectionFactory);
		}
		return new RedisTokenStore(redisTemplate);
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
