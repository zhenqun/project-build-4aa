package com.ido85.party.aaaa.mgmt.security.authentication.handler;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ido85.party.aaaa.mgmt.security.authentication.application.AssistUserApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import com.ido85.party.aaaa.mgmt.security.authentication.application.LoginLogApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserResources;
import com.ido85.party.aaaa.mgmt.security.constants.LogConstants;
import com.ido85.party.aaaa.mgmt.security.utils.DateUtils;
import com.ido85.party.aaaa.mgmt.security.utils.IPHostUtils;



/**
 * @author rongxj
 *
 */
@Named
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Inject
	private LoginLogApplication loginLogApplication;

	@Inject
	private AssistUserApplication assistUserApplication;
	
	@Inject
	private RedisTemplate<String, String> redisTemplate;
	
	@Inject
	private UserResources userResources;
	
	public LoginSuccessHandler(){
	}
	
	private RequestCache requestCache = new HttpSessionRequestCache();
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		//增加日志
		String ip = IPHostUtils.getRemoteHost(request);
		String hostname = request.getRemoteHost();
		try {
			String username = request.getSession(false).getAttribute("loginName").toString();
			deleteRedisLockKey(username);
			userResources.updateLashLoginDate(username, new Date());
			loginLogApplication.addLoginLog(ip,hostname,authentication.getName(),"登陆成功",LogConstants.LOGIN_SUCCESS);

			boolean hasAssistModule = assistUserApplication.getAssistIsModule();
			HttpSession session = request.getSession();
			session.setAttribute("hasAssistModule", hasAssistModule);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SavedRequest savedRequest = requestCache.getRequest(request, response);
			if (savedRequest == null) {
				super.onAuthenticationSuccess(request, response, authentication);
				return;
			}
			String targetUrlParameter = getTargetUrlParameter();
			if (isAlwaysUseDefaultTargetUrl()
					|| (targetUrlParameter != null && StringUtils.hasText(request
							.getParameter(targetUrlParameter)))) {
				requestCache.removeRequest(request, response);
				super.onAuthenticationSuccess(request, response, authentication);
				return;
			}
			clearAuthenticationAttributes(request);
			//将要跳转到的URL
			String targetUrl = savedRequest.getRedirectUrl();
			
			//跳转
			getRedirectStrategy().sendRedirect(request, response, targetUrl);
		}
	}
	
	private void deleteRedisLockKey(String username) {
		String userId = null;
		User u = userResources.getUserByAccount(username);
		if(null != u){
			userId = u.getId();
			String key = DateUtils.getDate()+"|"+userId;
			if(redisTemplate.hasKey(key)){
				redisTemplate.delete(key);
			}
		}
	}
	
	public void setRequestCache(RequestCache requestCache) {
		this.requestCache = requestCache;
	}
	
}
