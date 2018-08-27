package com.ido85.party.sso.platform.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageConfiguration {

	@Bean(name="messageSource")
	public ReloadableResourceBundleMessageSource messageSource(){
		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		source.setBasename("classpath:message");
		source.setCacheSeconds(120);
		source.setDefaultEncoding("utf-8");
		return source;
	}
}
