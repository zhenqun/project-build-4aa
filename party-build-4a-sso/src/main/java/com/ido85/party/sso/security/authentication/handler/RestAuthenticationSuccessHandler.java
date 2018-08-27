/**
 * 
 */
package com.ido85.party.sso.security.authentication.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

/**
 * @author rongxj
 *
 */
public class RestAuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
//		response.sendRedirect("");
		System.out.println("999999999999999999999999999999999999");
	}

}
