/**
 * 
 */
package com.ido85.party.sso.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.ido85.party.sso.dto.CenterUrlDto;
import com.ido85.party.sso.security.authentication.application.UserApplication;
import com.ido85.party.sso.security.authentication.repository.CenterUrlResources;
import com.ido85.party.sso.security.utils.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.LogFactory;
import org.apache.log4j.spi.LoggerFactory;
import org.codehaus.groovy.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.utils.StringUtils;
import sun.security.smartcardio.SunPCSC;

//import com.ido85.party.aaaa.mgmt.security.authentication.dto.InRegisterUser;



/**
 * 
 * @author rongxj
 *
 */
@Controller
@Slf4j
public class UIController {
	
	@Value("${personalZoneUrl}")
	private String personalZoneUrl;

	@Value("${http2https}")
	private String httpFlag;

	@Value("${mainUrl}")
	private String mainUrl;

	@Inject
	private UserApplication userApplication;

	@RequestMapping(path = {"/","/login"}, method = RequestMethod.GET)
	public String index(HttpServletRequest request,Model model){

		String JSESSIONID = request.getSession().getId();//获取当前JSESSIONID （不管是从主域还是二级域访问产生）
		System.out.println("获取当前JSESSIONID （不管是从主域还是二级域访问产生）");
		model.addAttribute("sessionId",mainUrl+"/sso/cologin?sessionId="+JSESSIONID);
		Object object = null;
		Authentication authentication = null;
		User user = null;
		SecurityContext context = SecurityContextHolder.getContext();
		if(null != context){
			authentication = context.getAuthentication();
		}
		if(null != authentication){
			object = authentication.getPrincipal();
		}else{
			SecurityContextImpl securityContextImpl = (SecurityContextImpl)request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
			if(null != securityContextImpl){
				object = securityContextImpl.getAuthentication().getPrincipal();
			}
		}
		if((object != null & object instanceof User)){
			user = (User)object;
		}
		if(null == user){
			log.info("========================="+request.getProtocol()+"------"+request.getScheme()+"------"+request.isSecure()+"----");
			model.addAttribute("httpFlag",httpFlag);
			return "login";
		}
		String redirectUri = request.getParameter("web_url");
		if(!StringUtils.isNull(redirectUri)){
			return "redirect:"+redirectUri;
		}
		//分中心个性化配置
		String area = request.getHeader("X-Area");
		log.info("=============="+area);
		redirectUri = userApplication.getDefaultLoginUrl(area);
		if(StringUtils.isNull(redirectUri)){
			return "redirect:"+personalZoneUrl;
		}else{
			return "redirect:"+redirectUri;
		}
	}

	@RequestMapping(path = {"/register"}, method = RequestMethod.GET)
	public String register(){
		return "register";
	}

	//忘记密码1
	@RequestMapping(path = {"/forget"}, method = RequestMethod.GET)
	public String forget(Model model){		
		return "forget";
	}
//	
//	@RequestMapping(path = {"/unifiedLogout"}, method = RequestMethod.GET)
//	public String unifiedLogout(){		
//		return "unifiedLogout";
//	}
}
