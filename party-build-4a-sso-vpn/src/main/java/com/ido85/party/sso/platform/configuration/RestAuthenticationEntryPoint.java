/**
 * 
 */
package com.ido85.party.sso.platform.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * @author rongxj
 *
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		response.getWriter().write("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		response.getWriter().flush();
//		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	}

}
