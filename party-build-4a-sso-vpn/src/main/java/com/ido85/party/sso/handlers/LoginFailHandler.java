package com.ido85.party.sso.handlers;

import com.ido85.party.sso.security.authentication.application.LoginLogApplication;
import com.ido85.party.sso.security.authentication.dbsync.spring.InstanceFactory;
import com.ido85.party.sso.security.constants.LogConstants;
import com.ido85.party.sso.security.utils.IPHostUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Named
public class LoginFailHandler extends SimpleUrlAuthenticationFailureHandler{

	private String defaultFailureUrl;
	
	private boolean forwardToDestination = false;
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	private boolean allowSessionCreation = true;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		try {
			LoginLogApplication loginLogApplication = InstanceFactory.getInstance(LoginLogApplication.class);// 获得日志记录的实例
			String ip = IPHostUtils.getRemoteHost(request);
			String username = request.getParameter("username");
			String hostname = request.getRemoteHost();
			String clientName = null;// 客户端名称
			if(null != request.getSession().getAttribute("CLIENTNAME")){
				clientName = request.getSession().getAttribute("CLIENTNAME").toString();
				loginLogApplication.addLoginLog(ip,clientName,username,exception.getMessage(),LogConstants.LOGIN_FAIL);// 添加登录失败的日志 LogConstants有失败信息编码
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (defaultFailureUrl == null) {
			logger.debug("No failure URL set, sending 401 Unauthorized error");

			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"Authentication Failed: " + exception.getMessage());// 重新定义一个页面异常信息  异常编码和异常信息

		}
		else {
			saveException(request, exception);

			if (forwardToDestination) {// 如果允许到目的地
				logger.debug("Forwarding to " + defaultFailureUrl);

				request.getRequestDispatcher(defaultFailureUrl)// 跳转到登录失败的页面
						.forward(request, response);
			}
			else {// 不允许到默认的目的地
				logger.debug("Redirecting to " + defaultFailureUrl);
				redirectStrategy.sendRedirect(request, response, defaultFailureUrl);// 跳转到失败登录页面
			}
		}
	}

	public LoginFailHandler() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoginFailHandler(String defaultFailureUrl) {
		setDefaultFailureUrl(defaultFailureUrl);
	}
	
	/**
	 * The URL whicbe used as the failure destination.
	 *
	 * @param defaultFailureUrl the failure URL, for example "/loginFailed.jsp".
	 */
	public void setDefaultFailureUrl(String defaultFailureUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(defaultFailureUrl), "'"
				+ defaultFailureUrl + "' is not a valid redirect URL");
		this.defaultFailureUrl = defaultFailureUrl;
	}

	protected boolean isUseForward() {
		return forwardToDestination;
	}

	/**
	 *
	 * If set to <tt>true</tt>, performs a forward to the failure destination URL instead
	 * of a redirect. Defaults to <tt>false</tt>.
	 */
	public void setUseForward(boolean forwardToDestination) {
		this.forwardToDestination = forwardToDestination;
	}

	/**
	 * Allows overriding of the behaviour when redirecting to a target URL.
	 */
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	protected RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	protected boolean isAllowSessionCreation() {
		return allowSessionCreation;
	}

	public void setAllowSessionCreation(boolean allowSessionCreation) {
		this.allowSessionCreation = allowSessionCreation;
	}

}
