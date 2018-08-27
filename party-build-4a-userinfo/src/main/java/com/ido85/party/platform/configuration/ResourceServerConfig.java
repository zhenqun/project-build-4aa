package com.ido85.party.platform.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
@EnableResourceServer
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Value("${security.oauth2.resource.resourceId}")
	private String RESOURCE_ID;

//	@Autowired
//	private DataSource dataSource;

	@Override
	public void configure(HttpSecurity http) throws Exception {
//		http.requestMatchers().antMatchers("/**").and().authorizeRequests()
//				.antMatchers("/").permitAll().anyRequest().authenticated();
		http.authorizeRequests().antMatchers().permitAll().anyRequest().authenticated();
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(RESOURCE_ID);
	}

	// @Bean
	// public TokenStore tokenStore() {
	// return new JdbcTokenStore(dataSource);
	// }

//	@Autowired
//	private DefaultTokenServices tokenServices;

//	@Bean
//	public TokenStore tokenStore() {
//		return new JwtTokenStore(accessTokenConverter());
//	}
//
//	@Bean
//	public JwtAccessTokenConverter accessTokenConverter() {
//		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
////		jwtAccessTokenConverter
////				.setAccessTokenConverter(getDefaultAccessTokenConverter());
//		jwtAccessTokenConverter.setVerifierKey(pubKey);
//		try {
//			jwtAccessTokenConverter.afterPropertiesSet();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return jwtAccessTokenConverter;
//	}

//	@Bean
//	AccessTokenConverter getDefaultAccessTokenConverter() {
//		DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();
//		defaultAccessTokenConverter
//				.setUserTokenConverter(getCustomUserAuthenticationConvertor());
//		return defaultAccessTokenConverter;
//	}

//	@Bean
//	UserAuthenticationConverter getCustomUserAuthenticationConvertor() {
//		return new CustomUserAuthenticationConvertor();
//	}

//	@Bean
//	public DefaultTokenServices defaultTokenServices() throws Exception {
//		final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
//		defaultTokenServices.setTokenEnhancer(accessTokenConverter());
//		defaultTokenServices.setTokenStore(tokenStore());
//		return defaultTokenServices;
//	}

	/**
	 * 
	 * 远程token验证（链接oauth2服务器进行验证的方式）
	 * @param checkTokenUrl
	 * @param clientId
	 * @param clientSecret
	 * @return
	 */
	@Bean
	public ResourceServerTokenServices remoteTokenServices(final @Value("${security.oauth2.server.checkTokenUri}") String checkTokenUrl,
			final @Value("${security.oauth2.server.resourceId}") String clientId,
			final @Value("${security.oauth2.server.resourceSecret}") String clientSecret) {
		final RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
		remoteTokenServices.setCheckTokenEndpointUrl(checkTokenUrl);
		remoteTokenServices.setClientId(clientId);
		remoteTokenServices.setClientSecret(clientSecret);
//		remoteTokenServices.setAccessTokenConverter(accessTokenConverter());
		return remoteTokenServices;
	}
}
