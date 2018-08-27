package com.ido85.party.sso.handlers;

import com.ido85.party.sso.security.authentication.application.LoginLogApplication;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.constants.LogConstants;
import com.ido85.party.sso.security.utils.DateUtils;
import com.ido85.party.sso.security.utils.IPHostUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Inject
	private LoginLogApplication loginLogApplication;

	@Inject
	private RedisTemplate<String, String> redisTemplate;

	@Inject
	private UserResources userResources;

	@Value("${ssoUrl}")// 这里也是在yml 配置文件中进行了配置
	private String ssoUrl;

	public LoginSuccessHandler() {
	}

	private RequestCache requestCache = new HttpSessionRequestCache();
        /** 以下主要是在登录验证成功后做一些日志记录的工作 **/
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		// 增加日志
		String ip = IPHostUtils.getRemoteHost(request);
		try {
			String clientName = null;
			String username = request.getSession().getAttribute("loginName").toString();
			if(null != request.getSession().getAttribute("CLIENTNAME")){
				clientName = request.getSession().getAttribute("CLIENTNAME").toString();
				loginLogApplication.addLoginLog(ip,clientName,authentication.getName(), "登陆成功", LogConstants.LOGIN_SUCCESS);
			}else{
				loginLogApplication.addLoginLog(ip,"虚拟专网工作台",authentication.getName(), "登陆成功", LogConstants.LOGIN_SUCCESS);
			}
			deleteRedisLockKey(username);
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
					|| (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
				requestCache.removeRequest(request, response);
				super.onAuthenticationSuccess(request, response, authentication);
				return;
			}
			clearAuthenticationAttributes(request);
			// 将要跳转到的URL
			String targetUrl = savedRequest.getRedirectUrl();
//			String refer = request.getHeader("Referer");
			String refer = null;
			if(!com.ido85.party.sso.security.utils.StringUtils.isNull(targetUrl)){
				refer = targetUrl.substring(targetUrl.length()-6,targetUrl.length());
				System.out.println(refer);
				if("modify".equals(refer)){
					targetUrl = ssoUrl;
				}
			}
			// 跳转
			getRedirectStrategy().sendRedirect(request, response, targetUrl);
		}
	}

	public void setRequestCache(RequestCache requestCache) {
		this.requestCache = requestCache;
	}

	private void deleteRedisLockKey(String username) {// 登录成功后 对redis 中的用户锁定信息进行清除
		String userId = null;
		User u = userResources.getUserbyAccountTelIdcard(username);
		if (null != u) {
			userId = u.getId();
			String key = DateUtils.getDate() + "|" + userId;
			if (redisTemplate.hasKey(key)) {
				redisTemplate.delete(key);
			}
		}
	}

}
