package com.ido85.party.sso.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class PreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
    public static final String SSO_TOKEN = "x-auth-token";
    public static final String SSO_CREDENTIALS = "N/A";

    public PreAuthenticationFilter(AuthenticationManager authenticationManager) {
        setAuthenticationManager(authenticationManager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader(SSO_TOKEN);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return SSO_CREDENTIALS;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    		throws IOException, ServletException {
    	HttpServletRequest req = (HttpServletRequest) request;
    	String path = req.getRequestURI();
    	String grant_type = null;
    	if("/sso/oauth/token".equals(path)){
    		grant_type = req.getParameter("grant_type");
    	}
    	if("refresh_token".equals(grant_type)){
    		chain.doFilter(request, response);
    	}else{
    		super.doFilter(request, response, chain);
    	}
    }
}
