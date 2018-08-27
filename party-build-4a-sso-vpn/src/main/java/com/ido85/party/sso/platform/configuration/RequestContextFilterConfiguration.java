package com.ido85.party.sso.platform.configuration;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.filter.RequestContextFilter;

@Configuration
public class RequestContextFilterConfiguration {

	@Bean

	@ConditionalOnMissingBean(RequestContextFilter.class)

	public RequestContextFilter requestContextFilter() {

		return new RequestContextFilter();

	}

	@Bean

	public FilterRegistrationBean requestContextFilterChainRegistration(

			@Qualifier("requestContextFilter") Filter securityFilter) {

		FilterRegistrationBean registration = new FilterRegistrationBean(securityFilter);

		registration.setOrder(SessionRepositoryFilter.DEFAULT_ORDER + 1);

		registration.setName("requestContextFilter");

		return registration;

	}
}
