package com.ido85.party.sso.filter;

import com.ido85.party.sso.security.authentication.application.CommonApplication;
import com.ido85.party.sso.security.oauth2.domain.PlatformClientDetails;
import com.ido85.party.sso.security.utils.StringUtils;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ClientRedirectFilter implements Filter{

	@Inject
	private CommonApplication commonApplication;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("过滤器====init");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
//		System.out.println("过滤器====do");  
		HttpServletRequest r = (HttpServletRequest) request;
		HttpSession session = r.getSession();
//		DefaultSavedRequest savedRequest = (DefaultSavedRequest)session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
//		if(savedRequest != null && savedRequest.getParameterMap().get("CLIENTNAME") == null){
//			String[] params = savedRequest.getParameterMap().get("client_id");
//			
//			if(params!=null && params.length > 0){
//				String clientId = params[0];
//				PlatformClientDetails clientDetails = clientRepository.getClientByClientId(clientId);
//				if(null != clientDetails){
//					String clientName = clientDetails.getClientName();
//					savedRequest.getParameterMap().put("CLIENTNAME", new String[]{clientName});
//				}
//				
//			}
//		}
		String clientName = null;
		String clientId = request.getParameter("client_id");
		if(!StringUtils.isNull(clientId)){
			PlatformClientDetails clientDetails = commonApplication.getClientByClientId(clientId);
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
		// TODO Auto-generated method stub
		System.out.println("过滤器====destroy");  
	}

}
