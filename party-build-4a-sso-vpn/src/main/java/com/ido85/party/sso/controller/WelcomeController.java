package com.ido85.party.sso.controller;

import com.ido85.party.sso.dto.ApplicationCategoryDto;
import com.ido85.party.sso.dto.index.NoticeDto;
import com.ido85.party.sso.dto.index.NoticeQueryParam;
import com.ido85.party.sso.security.authentication.application.CommonApplication;
import com.ido85.party.sso.security.authentication.application.IndexApplication;
import com.ido85.party.sso.security.authentication.application.OrgApplication;
import com.ido85.party.sso.security.authentication.domain.Notice;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.utils.RelativeDateFormat;
import com.ido85.party.sso.security.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 相关参数都在yml 配置文件中进行了配置 **/


@Controller
public class WelcomeController {
	
	@Autowired
	private OrgApplication orgApp;

	@Autowired
	private CommonApplication commonApp;

	@Autowired
	private IndexApplication indexApp;

	@Value("${userLogoUrl}")
	private String userLogoUrl;

	@Value("${defaultUserLogoUrl}")
	private String defaultUserLogoUrl;

	@Value("${safeCenterUrl}")
	private String safeCenterUrl = "";

	@RequestMapping(path = {"/","/index"}, method = RequestMethod.GET)
	public String index(Model model,Principal principal,HttpServletRequest request) throws UnsupportedEncodingException {
		SecurityContextImpl securityContextImpl = (SecurityContextImpl)request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
		System.out.println("访问地址  '/' 或者'/index'");

		if(null == securityContextImpl){
			return "login";
		}
		User user = (User)securityContextImpl.getAuthentication().getPrincipal();
//		Org org = orgApp.getOrgById(user.getOrgId());
//		if(null != org){
//			model.addAttribute("orgName",org.getOrgName() );
//		}
		List<ApplicationCategoryDto> apps = commonApp.applicationQuery("0", user.getId());
		model.addAttribute("apps", apps);


		NoticeQueryParam noticeParam = new NoticeQueryParam();
		noticeParam.setPageNo(1);
		noticeParam.setPageSize(6);
		List<NoticeDto> notices = indexApp.noticeQuery(noticeParam);
		List<Map<String, Object>> noticeList = new ArrayList<>();
		if (null != notices) {
			noticeList = notices
				.stream()
				.map(x -> {
					Map<String, Object> notice = new HashMap<>();
					notice.put("noticeId", x.getNoticeId());
					notice.put("noticeTitle", x.getNoticeTitle());
					notice.put("relativeReleaseDate", RelativeDateFormat.format(x.getReleaseDate()));
					return notice;
				})
				.collect(Collectors.toList());
		}
		model.addAttribute("notices", noticeList);
		model.addAttribute("lastLoginDate", RelativeDateFormat.format(user.getLastLoginDate()));
		if(!StringUtils.isNull(user.getLogo())){
			model.addAttribute("userLogo", userLogoUrl+user.getLogo());
		}else{
			model.addAttribute("userLogo", defaultUserLogoUrl);
		}
		return "index";
	}

	@RequestMapping(path = "/notice/{noticeId}", method = RequestMethod.GET)
	public String noticeItem(@PathVariable("noticeId") String noticeId, Model model) {
		Notice notice = indexApp.noticeDetail(noticeId);

		model.addAttribute("notice", notice);
		return "notice-item";
	}
	
	@RequestMapping(path = {"/register"}, method = RequestMethod.GET)
	public String register(Model model){
		model.addAttribute("safeCenterUrl", safeCenterUrl);
		model.addAttribute("content", "Hello World");
		return "register";
	}
	
	@RequestMapping(path = {"/success"}, method = RequestMethod.GET)
	public String success(Model model){
		
		return "success";
	}
	
	@RequestMapping(path = {"/modify"}, method = RequestMethod.GET)
	public String modify(Model model){		
		return "modify";
	}

	@RequestMapping(path = {"/modify-vpn"}, method = RequestMethod.GET)
	public String modifyVpn(Model model){		
		return "modify-vpn";
	}
	
	@RequestMapping(path = {"/head-pic"}, method = RequestMethod.GET)
	public String head(Model model){		
		return "head-pic";
	}
	
	@RequestMapping(path = {"/forget"}, method = RequestMethod.GET)
	public String forget(Model model){		
		return "forget";
	}
	
	@RequestMapping(path = {"/login"}, method = RequestMethod.GET)
	public String login(Model model,Principal principal,HttpServletRequest request){
		SecurityContextImpl securityContextImpl = (SecurityContextImpl)request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
		if(null != securityContextImpl){
			User user = (User)securityContextImpl.getAuthentication().getPrincipal();
			model.addAttribute("lastLoginDate", RelativeDateFormat.format(user.getLastLoginDate()));
			return "index";
		}
		return "login";
	}

	@RequestMapping(path = {"/modify-phone"}, method = RequestMethod.GET)
	public String changePhone(Model model){
		return "modify-phone";
	}
}
