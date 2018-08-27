package com.ido85.party.aaaa.mgmt.security.filters;

import com.ido85.party.aaaa.mgmt.security.authentication.application.UserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.exceptions.UserPasswordExpiredException;
import com.ido85.party.aaaa.mgmt.security.authentication.handler.LoginFailHandler;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserResources;
import com.ido85.party.aaaa.mgmt.security.exceptions.CaptchaValidateException;
import com.ido85.party.aaaa.mgmt.security.utils.DateUtils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class DefaultUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private RememberMeServices rememberMeServices = new NullRememberMeServices();
//	private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler("/login");
	private AuthenticationFailureHandler failureHandler = new LoginFailHandler("/login");
	public static final String SPRING_SECURITY_FORM_VALIDATE_CODE_KEY = "validateCode";
	//	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
	//	public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
	//
	//	private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
	//	private String passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;
	private String validateCodeParameter = SPRING_SECURITY_FORM_VALIDATE_CODE_KEY;
	@Inject
	private RedisTemplate<String, String> redisTemplate;
	@Inject
	private UserResources userResources;
	@Inject
	private UserApplication userApp;
	@Override  
	public Authentication attemptAuthentication(HttpServletRequest request,  
			HttpServletResponse response) throws AuthenticationException {  
		if (!request.getMethod().equals("POST")) {
			log.info("Authentication method not supported: {}", request.getMethod());
			throw new AuthenticationServiceException(
					"Authentication method not supported: " + request.getMethod());
		}

		String username = obtainUsername(request);
		String password = obtainPassword(request);
		String validateCode = obtainValidateCode(request);

		request.getSession().setAttribute("loginName", username);
		
		if (username == null) {
			username = "";
		}
		username = username.toUpperCase();

		if (password == null) {
			password = "";
		}

		if (null == validateCode) {
			validateCode = "";
		}

		String genCaptcha = (String)request.getSession().getAttribute("validateCode");
		log.info("开始校验验证码，生成的验证码为：{} ，输入的验证码为：{}", genCaptcha, validateCode); 

		if( !genCaptcha.equalsIgnoreCase(validateCode)){
			log.info("llz======验证码错误!!");
			throw new CaptchaValidateException("验证码错误");
		}
		//管理端用户采取强密码策略
//		Integer day = StringUtils.toInteger(userApp.checkUserPwd(username));
//		if(day>30){
//			throw new UserPasswordExpiredException("您的密码过期，请联系管理员重置!");
//		}else if(day<=10 && day>0){
//			request.getSession().setAttribute("pwdInfo", "您的密码还有 "+day+" 天过期，请及时修改密码!密码过期后账号将不能使用!");
//		}
		username = username.trim();

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				username, password);

		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);

		return this.getAuthenticationManager().authenticate(authRequest);

	}  

	protected String obtainValidateCode(HttpServletRequest request) {
		return request.getParameter(validateCodeParameter);
	}

	@Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        logger.debug("unsuccessfulAuthentication(HttpServletRequest, HttpServletResponse, AuthenticationException) - start"); //$NON-NLS-1$
        SecurityContextHolder.clearContext();

		if (logger.isDebugEnabled()) {
			logger.debug("Authentication request failed: " + failed.toString());
			logger.debug("Updated SecurityContextHolder to contain null Authentication");
			logger.debug("Delegating to authentication failure handler " + failureHandler);
		}
		
		failureHandler = new LoginFailHandler("/login?error");
		
		if(failed instanceof CaptchaValidateException){
			request.getSession(false).setAttribute("error", "验证码错误");
			failureHandler.onAuthenticationFailure(request, response, failed);
		}else if(failed instanceof InternalAuthenticationServiceException){
			request.getSession(false).setAttribute("error", failed.getMessage());
			failureHandler.onAuthenticationFailure(request, response, failed);
		}else if(failed instanceof UserPasswordExpiredException){
			request.getSession(false).setAttribute("error", failed.getMessage());
			failureHandler.onAuthenticationFailure(request, response, failed);
		}else if(failed instanceof BadCredentialsException){
			//登录锁定功能
//			Integer cnt = 0;
//			String username = request.getSession(false).getAttribute("loginName").toString();
//			User u = userResources.getUserByIdcardTelAccount(username);
//			String userId = null;
//			if(null != u){
//				userId = u.getId();
//				cnt = loginLock(userId);
//			}
//			if(cnt<5){
//				request.getSession(false).setAttribute("error", "用户名或密码错误，已经错误输入 "+cnt+" 次,连续错误5次将被锁定");
//				failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("用户名或密码错误，已经错误输入"+cnt+" 次,连续错误5次将被锁定"));
//			}else if(cnt >= 5){
//				//执行锁定操作
//				request.getSession(false).setAttribute("error", "用户名或密码错误，已经错误输入 "+cnt+" 次,账号被锁定");
//				failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("用户名或密码错误，已经错误输入"+cnt+" 次,账号被锁定"));
//				userResources.lockUser(userId);
//			}
			request.getSession(false).setAttribute("error", "用户名或密码错误");
			failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("用户名或密码错误"));
		}
//		rememberMeServices.loginFail(request, response);
		request.setAttribute("error", "用户名或密码错误");		

		
//        super.unsuccessfulAuthentication(request, response, failed);
        logger.debug("登录失败！");
        logger.debug("unsuccessfulAuthentication(HttpServletRequest, HttpServletResponse, AuthenticationException) - end"); //$NON-NLS-1$
    }
	
	private Integer loginLock(String userId) {
		String key = DateUtils.getDate()+"|"+userId;
		Integer cnt = 0;
		if(redisTemplate.hasKey(key)){
			String redisValue = redisTemplate.opsForValue().get(key).toString();
			cnt = Integer.parseInt(redisValue) + 1;
			redisTemplate.opsForValue().set(key, cnt.toString());
		}else{
			redisTemplate.persist(key);
			redisTemplate.expireAt(key, DateUtils.getDateAfter(1));
			redisTemplate.opsForValue().set(key, "1");
			cnt = 1;
		}
		return cnt;
	}
}
