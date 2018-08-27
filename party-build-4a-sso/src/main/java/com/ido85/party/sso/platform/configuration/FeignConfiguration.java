/**
 *
 */
package com.ido85.party.sso.platform.configuration;

import feign.Request.Options;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

/**
 * @author rongxj
 *
 */
@Configuration
public class FeignConfiguration {

	@Inject
	Environment environment;


	@Bean
	public Retryer retryer() {
		return new Retryer.Default(0, 0, 0);
	}

	@Bean
	public Options options(){
		return new Options(10 * 1000, 10 * 1000);
	}

	@Bean
	public ErrorDecoder errorDecoder(){
		return new FeignClientErrorDecoder();
	}

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	/*
	 * 移动到 OauthClientConfiguration
	@LoadBalanced
	@Bean
	public OAuth2RestTemplate oauthTemplate(){;
		return new OAuth2RestTemplate(clientResourceDetails());
	}


	@Bean
	public OAuth2ProtectedResourceDetails clientResourceDetails() {
		ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
		resource.setAccessTokenUri(environment.getProperty("security.oauth2.client.accessTokenUri"));
		resource.setClientId(environment.getProperty("security.oauth2.client.clientId"));
		resource.setClientSecret(environment.getProperty("security.oauth2.client.clientSecret"));
		return resource;
	}*/




	//验证多线程模式feign上下文传递，失败
//	@Bean
//	public FilterRegistrationBean requestContextFilterRegistration() {
//		FilterRegistrationBean filter = new FilterRegistrationBean();
//		filter.setFilter(requestContextFilter());
//		filter.setOrder(0);
//		return filter;
//	}
//
//	@Bean
//	public RequestContextFilter requestContextFilter() {
//		RequestContextFilter filter = new OrderedRequestContextFilter();
//		filter.setThreadContextInheritable(true);
//		return filter;
//	}
}
