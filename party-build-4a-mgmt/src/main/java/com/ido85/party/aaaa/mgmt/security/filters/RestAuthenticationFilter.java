/**
 * 
 */
package com.ido85.party.aaaa.mgmt.security.filters;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.Assert;

import com.ido85.party.aaaa.mgmt.security.wrapper.JsonRequestWrapper;



/**
 * @author rongxj
 *
 */
public class RestAuthenticationFilter extends
		AbstractAuthenticationProcessingFilter {
	
	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
	public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

	private String usernameKey = SPRING_SECURITY_FORM_USERNAME_KEY;
	private String passwordKey = SPRING_SECURITY_FORM_PASSWORD_KEY;
	private boolean postOnly = true;
	
	public RestAuthenticationFilter(){
		this("/api/login");
	}

	protected RestAuthenticationFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#attemptAuthentication(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
		if (postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException(
					"Authentication method not supported: " + request.getMethod());
		}
		Map req = new JsonRequestWrapper(request).getBody();
		
		String username = obtainUsername(req);
		String password = obtainPassword(req);

		if (username == null) {
			username = "";
		}

		if (password == null) {
			password = "";
		}

		username = username.trim();

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				username, password);

		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);

		return this.getAuthenticationManager().authenticate(authRequest);
	}
	
	@SuppressWarnings("rawtypes")
	protected String obtainPassword(Map request) {
		return String.valueOf(request.get(passwordKey));
	}

	@SuppressWarnings("rawtypes")
	protected String obtainUsername(Map request) {
		return String.valueOf(request.get(usernameKey));
	}

	protected void setDetails(HttpServletRequest request,
			UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

	public void setUsernameKey(String usernameKey) {
		Assert.hasText(usernameKey, "Username Key must not be empty or null");
		this.usernameKey = usernameKey;
	}

	public void setPasswordKey(String passwordKey) {
		Assert.hasText(passwordKey, "Password Key must not be empty or null");
		this.passwordKey = passwordKey;
	}

	public void setPostOnly(boolean postOnly) {
		this.postOnly = postOnly;
	}

	public final String getUsernameKey() {
		return usernameKey;
	}

	public final String getPasswordKey() {
		return passwordKey;
	}

}
