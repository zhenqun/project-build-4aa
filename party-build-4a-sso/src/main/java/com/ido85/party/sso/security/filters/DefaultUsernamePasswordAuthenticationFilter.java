package com.ido85.party.sso.security.filters;

import com.ido85.party.sso.handlers.LoginFailHandler;
import com.ido85.party.sso.security.authentication.application.UserApplication;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.exceptions.CaptchaValidateException;
import com.ido85.party.sso.security.utils.DateUtils;
import com.ido85.party.sso.security.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.Date;

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
	@Qualifier(value="stringTemplate")
	private RedisTemplate<String, String> redisTemplate;// 这里是为了引入 RedisAutoConfiguration
	                                                    // 文件中中名字为 stringTemplate 的bean 这个bean 被spring 管理
	@Inject
	private UserApplication userApplication;
	@Override  
	public Authentication attemptAuthentication(HttpServletRequest request,  
			HttpServletResponse response) throws AuthenticationException {  
		if (!request.getMethod().equals("POST")) {                            // 登录的请求必须为post
			log.info("Authentication method not supported: {}", request.getMethod());//这里的 日志类 log 是在 @Slf4j 里面的变量
			throw new AuthenticationServiceException(
					"Authentication method not supported: " + request.getMethod());
		}


		String username = obtainUsername(request);// 调用父类中的方法获取用户名和密码
		String password = obtainPassword(request);
		String validateCode = obtainValidateCode(request);

		log.info("=======登录:"+username+"密码:"+password);
		request.getSession().setAttribute("loginName", username); // 将 用户名存储到 Session 中
		
		if (username == null) {
			username = "";
		}

		if (password == null) {
			password = "";
		}

		if (null == validateCode) {
			validateCode = "";
		}

		String genCaptcha = (String)request.getSession().getAttribute("validateCode");// 注意这种日志记录的打印格式
		log.info("开始校验验证码，生成的验证码为：{} ，输入的验证码为：{}", genCaptcha, validateCode);// 始校验验证码，生成的验证码为：AEKH ，输入的验证码为：AEKH

//		if(validateCode.equals("85ido")){
//			username = username.trim();
//
//			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
//					username, password);
//
//			// Allow subclasses to set the "details" property
//			setDetails(request, authRequest);
//
//			return this.getAuthenticationManager().authenticate(authRequest);
//		}
		if( !genCaptcha.equalsIgnoreCase(validateCode)){
			log.info("llz======验证码错误!!");
			throw new CaptchaValidateException("验证码错误");
		}

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
		
//		failureHandler = new SimpleUrlAuthenticationFailureHandler("/login?error");
		failureHandler = new LoginFailHandler("/login?error");
		
		if(failed instanceof CaptchaValidateException){
			request.getSession(false).setAttribute("error", "验证码错误");
			failureHandler.onAuthenticationFailure(request, response, failed);
		}else  if(failed instanceof InternalAuthenticationServiceException){
			request.getSession(false).setAttribute("error", failed.getMessage());
			failureHandler.onAuthenticationFailure(request, response, failed);
		}else if(failed instanceof BadCredentialsException){
			//登录锁定功能
			long cnt = 0;
			String username = request.getSession(false).getAttribute("loginName").toString();
			User u = userApplication.getUserbyAccountTelIdcard(username,StringUtils.getIDHash(username));//这里是用户名和用户名的hash
			String userId = null;
			if(null != u){
				userId = u.getId();
				cnt = loginLock(userId);
				if(cnt<5){
					request.getSession(false).setAttribute("error", "用户名或密码错误，已输错"+cnt+"次,连续输错5次将被锁定。");
					failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("用户名或密码错误，已输错"+cnt+"次,连续输错5次将被锁定。"));
				}else if(cnt >= 5){
					//执行锁定操作
					request.getSession(false).setAttribute("error", "用户名或密码错误，已输错"+cnt+"次,账号被锁定30分钟。");
					failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("用户名或密码错误，已输错"+cnt+"次,账号被锁定30分钟。"));
					userApplication.setUserExpireDate(userId);
				}
			}else{
				request.getSession(false).setAttribute("error", "用户名或密码错误");
				failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("用户名或密码错误"));
			}
		}
//		rememberMeServices.loginFail(request, response);
		request.setAttribute("error", "其他错误");		

		
//        super.unsuccessfulAuthentication(request, response, failed);
        logger.debug("登录失败！");
        logger.debug("unsuccessfulAuthentication(HttpServletRequest, HttpServletResponse, AuthenticationException) - end"); //$NON-NLS-1$
    }

	private long loginLock(String userId) {
		String key = DateUtils.getDate()+"|"+userId;
		long value = redisTemplate.opsForValue().increment(key,1);
		if(value == 1){
			redisTemplate.expireAt(key, DateUtils.getDateAfterSecond(24*60*60));// 这里是锁定的时间
		}
		if(value == 5){
			if(redisTemplate.hasKey(key)){
				redisTemplate.delete(key);
			}
		}
//		redisTemplate.expireAt(key, DateUtils.getDateAfter(1));

	/*	if(redisTemplate.hasKey(key)){
//			redisTemplate.opsForValue().increment(key, 1);
			String redisValue = redisTemplate.opsForValue().get(key).toString();
			cnt = Integer.parseInt(redisValue) + 1;
			redisTemplate.opsForValue().set(key, cnt.toString());
		}else{
			redisTemplate.persist(key);
			redisTemplate.expireAt(key, DateUtils.getDateAfter(1));
			redisTemplate.opsForValue().set(key, "1");
			cnt = 1;
		}*/
		return value;
	}
}
