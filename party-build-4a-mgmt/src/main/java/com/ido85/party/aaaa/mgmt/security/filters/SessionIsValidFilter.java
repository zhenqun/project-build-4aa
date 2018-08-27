package com.ido85.party.aaaa.mgmt.security.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;

//@WebFilter(filterName="sessionFilter",urlPatterns={"/manage/**","/update/**"})
public class SessionIsValidFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("创建---------");
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("执行-0---------");
		boolean isLogin = SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
		if(isLogin){
			chain.doFilter(request, response);
		}else{
			((HttpServletResponse)response).sendRedirect("/login");
		}
	}

	@Override
	public void destroy() {
		System.out.println("销毁----------");
	}

}
