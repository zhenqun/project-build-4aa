package com.ido85.party.sso.handlers;

import com.ido85.party.sso.log.application.PersonLoginLogApplication;
import com.ido85.party.sso.log.constant.PersonLogConstant;
import com.ido85.party.sso.security.authentication.application.LoginLogApplication;
import com.ido85.party.sso.security.authentication.application.UserApplication;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.utils.DateUtils;
import com.ido85.party.sso.security.utils.IPHostUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author rongxj
 *
 */
@Named
@Slf4j
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Inject
	private LoginLogApplication loginLogApplication;
	
	@Inject
	private RedisTemplate<String, String> redisTemplate;
	
	@Inject
	private UserResources userResources;

	@Inject
	private UserApplication userApplication;

	@Inject
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@Inject
	private PersonLoginLogApplication personLoginLogApplication;

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
			String clientName = null;
			String username = request.getSession().getAttribute("loginName").toString();
			if(null != request.getSession().getAttribute("CLIENTNAME")) {
				clientName = request.getSession().getAttribute("CLIENTNAME").toString();
			}
			if(null==clientName || "".equals(clientName)){
				clientName = "个人专区";
			}
			final String finalClientId = clientName;
			threadPoolTaskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						deleteRedisLockKey(username);
						//往给个人专区的库里插入日志
						User user =  userApplication.getUserbyAccountTelIdcard(username, com.ido85.party.sso.security.utils.StringUtils.getIDHash(username));
//						//删除用户锁定时间
//						userApplication.clearUserExpireDate(user.getId());
						if(null != user){
							personLoginLogApplication.addLoginLog(user.getId(),user.getHash(), PersonLogConstant.LOGIN_LOG, finalClientId);
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	private void deleteRedisLockKey(String username) {
		String userId = null;
		User u = userApplication.getUserbyAccountTelIdcard(username,com.ido85.party.sso.security.utils.StringUtils.getIDHash(username));
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
