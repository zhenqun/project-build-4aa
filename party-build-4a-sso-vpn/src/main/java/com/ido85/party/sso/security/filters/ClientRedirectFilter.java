package com.ido85.party.sso.security.filters;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ido85.party.sso.security.authentication.domain.ClientExpand;
import com.ido85.party.sso.security.authentication.repository.ClientExpandResource;
import com.ido85.party.sso.security.oauth2.repository.ClientRepository;
import com.ido85.party.sso.security.utils.StringUtils;

public class ClientRedirectFilter implements Filter{

	@Inject
	private ClientExpandResource clientExpandResource;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("过滤器====init");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest r = (HttpServletRequest) request;
		HttpSession session = r.getSession();
		String clientName = null;
		String clientId = request.getParameter("client_id");
		if(!StringUtils.isNull(clientId)){
			ClientExpand clientDetails = clientExpandResource.getClientById(clientId);
			if(null != clientDetails){
				clientName = clientDetails.getClientName();
				if(!StringUtils.isNull(clientName)){
					session.setAttribute("CLIENTNAME", clientName);
				}else{
					session.setAttribute("CLIENTNAME", null);
				}
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		System.out.println("过滤器====destroy");  
	}

}
