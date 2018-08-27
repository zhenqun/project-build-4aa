package com.ido85.party.sso.security.filters;

import cfca.svs.api.ClientEnvironment;
import cfca.svs.api.SVBusiness;
import cfca.svs.api.util.XmlUtil;
import com.ido85.party.sso.handlers.LoginFailHandler;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.provider.DefaultUsernamePasswordAuthenticationToken;
import com.ido85.party.sso.security.authentication.provisioning.PlateformUserDetailsManager;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.exceptions.CaLoginValidateFailException;
import com.ido85.party.sso.security.exceptions.CaptchaValidateException;
import com.ido85.party.sso.security.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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
	private AuthenticationFailureHandler failureHandler = new LoginFailHandler("/login");// 处理错误信息用的
	public static final String SPRING_SECURITY_FORM_VALIDATE_CODE_KEY = "validateCode";
	//	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
	//	public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
	//
	//	private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
	//	private String passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;
	private String validateCodeParameter = SPRING_SECURITY_FORM_VALIDATE_CODE_KEY;
	@Inject
	@Qualifier(value="defaultRedisTemplate")
	private RedisTemplate<String, String> redisTemplate; // 这里是为了引入 RedisAutoConfiguration
	// 文件中中名字为 stringTemplate 的bean 这个bean 被spring 管理
	@Inject
	private UserResources userResources;
	@Inject
	private PlateformUserDetailsManager userManager;
	@Value("${ca.client-ip}")
	private String clientIp;
	@Value("${ca.client-port}")
	private String clientPort;
	@Override  
	public Authentication attemptAuthentication(HttpServletRequest request,  
			HttpServletResponse response) throws AuthenticationException {
		if (!request.getMethod().equals("POST")) {                            // 登录的请求必须为post
			log.info("Authentication method not supported: {}", request.getMethod());//这里的 日志类 log 是在 @Slf4j 里面的变量
			throw new AuthenticationServiceException(
					"Authentication method not supported: " + request.getMethod());
		}
		/**
		 * CA认证流程
		 */
		String loginType = request.getParameter("loginType");
		log.error(loginType+"+=========================");
		if("ca".equals(loginType)){
			try {
				// 登录名获取
				ClientEnvironment.initClientEnvironment("config");
				SVBusiness s = new SVBusiness();
				String idCard = request.getParameter("idCard");
				String sign = request.getParameter("sign");
				String source = request.getParameter("source");
		        String r = s.sm2P7DetachVerifySign(source.getBytes(), sign, "-1");
		        String typeInfo = "SM2 P7 分离原文验签  ";
		        boolean result = dealResponse(r,typeInfo);
		        if(result){
		        	User user = userResources.getUserByIdcard(idCard);
		        	if(null != user){
						DefaultUsernamePasswordAuthenticationToken authRequest = new DefaultUsernamePasswordAuthenticationToken(
								user.getUsername(), "85ido&CAPWD","ca");
						this.setDetails(request, authRequest);
						request.getSession().setAttribute("loginName", user.getUsername());
						return this.getAuthenticationManager().authenticate(authRequest);
					}else{
						throw new CaLoginValidateFailException("改证书没有绑定用户");
					}
		        }
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}



		String username = obtainUsername(request);// 调用父类中的方法获取用户名和密码
		String password = obtainPassword(request);
		String validateCode = obtainValidateCode(request);

		log.info("=======登录:"+username+"密码:"+password);
		request.getSession().setAttribute("loginName", username); // 将 用户名存储到 Session 中

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

		request.getSession().setAttribute("loginName", username);
		String genCaptcha = (String)request.getSession().getAttribute("validateCode");
		log.info("开始校验验证码，生成的验证码为：{} ，输入的验证码为：{}", genCaptcha, validateCode); 

		if( !genCaptcha.equalsIgnoreCase(validateCode)){
			log.info("llz======验证码错误!!");
			throw new CaptchaValidateException("图片验证码错误");
		}

		username = username.trim();

		DefaultUsernamePasswordAuthenticationToken authRequest = new DefaultUsernamePasswordAuthenticationToken(
				username, password,"simple");

		// Allow subclasses to set the "details" property
		this.setDetails(request, authRequest);

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
		
		failureHandler = new LoginFailHandler("/login?error");// 登录不成功的时候  带验错误码登录
		
		if(failed instanceof CaptchaValidateException){
			request.getSession(false).setAttribute("error", "验证码错误");// 这里的参数 false  是用来指定是否重新生成 session
			failureHandler.onAuthenticationFailure(request, response, failed);
		}else  if(failed instanceof InternalAuthenticationServiceException){
			request.getSession(false).setAttribute("error", failed.getMessage());
			failureHandler.onAuthenticationFailure(request, response, failed);
		}else if(failed instanceof CaLoginValidateFailException){
			request.getSession(false).setAttribute("error", failed.getMessage());
			failureHandler.onAuthenticationFailure(request, response, failed);
		}else if(failed instanceof BadCredentialsException){
			//登录锁定功能
//			Integer cnt = 0;
//			String username = request.getSession(false).getAttribute("loginName").toString();
//			User u = userResources.getUserbyAccountTelIdcard(username);
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
		request.setAttribute("error", "其他错误");		

		
//        super.unsuccessfulAuthentication(request, response, failed);
        logger.debug("登录失败！");
        logger.debug("unsuccessfulAuthentication(HttpServletRequest, HttpServletResponse, AuthenticationException) - end"); //$NON-NLS-1$
    }
	
	protected void setDetails(HttpServletRequest request,
			DefaultUsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
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
	
	private boolean dealResponse(String response, String typeInfo) {
        System.out.println();
        if ("".equals(response)) {
            System.out.println("Socket 可能发生 通讯异常");
        }
        String errorCode = XmlUtil.getNodeText(response, "ErrorCode");
        if ("0".equals(errorCode)) {
            System.out.println(typeInfo + "成功");
            return true;
        } else {
            System.out.println(typeInfo + "失败");
            System.out.println("失败信息为:" + errorCode + " 错误信息为:" + XmlUtil.getNodeText(response, "ErrorDesc"));
            return false;
        }
    }
}
