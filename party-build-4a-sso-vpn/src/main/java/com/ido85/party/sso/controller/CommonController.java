package com.ido85.party.sso.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.ido85.party.sso.distribute.generator.IdGenerator;
import com.ido85.party.sso.dto.ApplicationCategoryDto;
import com.ido85.party.sso.dto.ApplicationDetailDto;
import com.ido85.party.sso.dto.ApplicationDto;
import com.ido85.party.sso.dto.SendMessageDto;
import com.ido85.party.sso.dto.SendMessageParam;
import com.ido85.party.sso.security.authentication.application.CommonApplication;
import com.ido85.party.sso.security.authentication.domain.MessageLog;
import com.ido85.party.sso.security.authentication.domain.SmTemplate;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.repository.MessageLogResources;
import com.ido85.party.sso.security.authentication.repository.SmTemplateResouces;
import com.ido85.party.sso.security.utils.ListUntils;
import com.ido85.party.sso.security.utils.SmsService;
import com.ido85.party.sso.security.utils.StringUtils;

@Controller
public class CommonController {
	
	@Inject
	private SmTemplateResouces smTemplateResouces;

	@Inject
	private CommonApplication commonApp;

	@Inject
	private TemplateEngine templateEngine;

	@Inject
	private SmsService smsService;

	@Value("${sendMessage.intervaltime}")
	private String intervaltime;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Inject
	private MessageLogResources messageLogResources;

	@Value("${ssoUrl}")
	private String ssoUrl;

	/**
	 * 应用通用 Sdk，包含九宫格，检查浏览器等通用操作，使用参数开启或关闭
	 * @param netType 网络类型，0 为 VPN区，1 为大组工网区
	 * @param insertFunctions 是否开启九宫格，默认为 true
	 * @param browserHappy 是否检查浏览器，默认为 0。0 为不检查浏览器，1 为检查浏览器，弹出的提醒可以关闭，2 为检查浏览器，弹出的提醒不能关闭
	 * @param request
	 * @return 生成的 js 代码
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(path="/application-sdk", method={RequestMethod.GET}, produces = { "application/javascript" })
	@ResponseBody
	public String applications(@QueryParam("type") String netType, @QueryParam("insertFunctions") Boolean insertFunctions, @QueryParam("browserHappy") Integer browserHappy, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		//获取当前登录用户
		if (StringUtils.isBlank(netType)) {
			netType = "0";
		}
		if (insertFunctions == null) {
			insertFunctions = true;
		}
		if (browserHappy == null) {
			browserHappy = 0;
		}

		String ifNoneMatch = request.getHeader("If-None-Match");
		String sessionId = request.getSession().getId();
		if (sessionId != null && sessionId.equals(ifNoneMatch)) {
			response.setStatus(HttpStatus.NOT_MODIFIED.value());
			return null;
		}
		response.setHeader("ETag", sessionId);

		User user = null;
		SecurityContextImpl securityContextImpl = (SecurityContextImpl)request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
		if(null == securityContextImpl){
			return "(function() { console.group('application-sdk'); console.warn('You should login first.'); console.groupEnd(); })();";
		}
		user = (User) securityContextImpl.getAuthentication().getPrincipal();

		String js = "(function() {";
		if (insertFunctions) {
			//0代表vpn区  1代表大组工内网
			List<ApplicationCategoryDto> acDtos = commonApp.applicationQuery(netType, user.getId());
			List<ApplicationDto> result = transformDto(acDtos);
			Context context = new Context();
			context.setVariable("appCategories", result);

			String application = this.templateEngine.process("applications", context);
			if (StringUtils.isNotEmpty(application)) {
				application = application.replaceAll("\\r?\\n", "");
			}
			js += "var html = '" + application + "';" +
					"$(window).on('load', function() {" +
						"var $container = $('[data-functions-container]');" +
						"var isHandled = $container.attr('data-is-handled');" +
						"if (typeof isHandled === 'undefined') {" +
							"$container.html(html);" +
							"$container.attr('data-is-handled', true);" +
						"}" +
					"});";
		}

		if (browserHappy > 0) {
			Context context = new Context();
			context.setVariable("showAfterInit", true);
			context.setVariable("allowClose", browserHappy.equals(1));
			String modal = this.templateEngine.process("modal/browser-modal", context);
			if (StringUtils.isNotEmpty(modal)) {
				modal = modal.replaceAll("\\n", "");
			}

			js += "function showBrowserModal() {" +
				"var modal = '" + modal + "';" +
				"$('body').append(modal);" +
			"}" +
			"$(window).on('load', function() {" +
				"var $bowser = document.createElement('script');" +
				"$bowser.addEventListener('load', function() {" +
					"if ((!bowser.chrome) && (!bowser.blink)) {" +
						"showBrowserModal();" +
					"}";
			if (browserHappy.equals(1)) {
				js += "$('body').on('click', '#browser-modal [data-dismiss=\"modal\"]', function() {" +
						"$('#browser-modal').remove();" +
					"});";
			}

			js += "});" +
				"$bowser.src = '" + ssoUrl + "/js/bowser.min.js';" +
				"document.getElementsByTagName('body')[0].appendChild($bowser);" +
			"});";
		}
		js += "})();";
		return js;
	}
	
	private List<ApplicationDto> transformDto(List<ApplicationCategoryDto> acDtos) {
		List<ApplicationDto> apps = new ArrayList<ApplicationDto>();
		ApplicationDto applicationDto = null;
		ApplicationDetailDto appDetails = null;
		//取出所有分类
		if(ListUntils.isNotNull(acDtos)){
			for(ApplicationCategoryDto acDto:acDtos){
				applicationDto = new ApplicationDto();
				applicationDto.setCategoryId(acDto.getCategoryId());
				applicationDto.setCategoryName(acDto.getCategoryName());
				applicationDto.setCategoryOrder(acDto.getCategoryOrder());
				applicationDto.setType(acDto.getType());
				if(!apps.contains(applicationDto)){
					List<ApplicationDetailDto> detailList = new ArrayList<ApplicationDetailDto>();
					applicationDto.setApps(detailList);
					apps.add(applicationDto);
				}
			}
		}
		apps = ListUntils.duplicateList(apps);
		//循环遍历所有应用
		if(ListUntils.isNotNull(acDtos)){
			for(ApplicationCategoryDto acDto:acDtos){
				Long cId = StringUtils.toLong(acDto.getCategoryId());
				appDetails = new ApplicationDetailDto();
				appDetails.setApplicationId(acDto.getCategoryId());
				appDetails.setApplicationImage(acDto.getApplicationImage());
				appDetails.setApplicationName(acDto.getApplicationName());
				appDetails.setApplicationOrder(acDto.getApplicationOrder());
				appDetails.setApplicationUrl(acDto.getApplicationUrl());
				for(ApplicationDto appDto:apps){
					Long categoryId = StringUtils.toLong(appDto.getCategoryId());
					if(cId.longValue() == categoryId.longValue()){
						appDto.getApps().add(appDetails);
					}
				}
			}
		}
		return apps;
	}

	/**
	 * 发送短信
	 * @param orgId
	 * @return
	 */
	@RequestMapping(path="/sendMessage", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> sendMessage(@Valid @RequestBody SendMessageParam param){
		Map<String,Object> map = new HashMap<String,Object>();
		String telephone = param.getTelephone();
		//限制连续发送
		boolean flag = smsService.isContinuitySend(telephone);
		if(flag){
			map.put("flag", "fail");
			map.put("message", "请您间隔"+intervaltime+"分钟后再次获取！");
			return map;
		}
		String type = param.getType();
		String verifycode = StringUtils.getRandomNum(6);
		MessageLog messageLog = new MessageLog();
		//查模板
		SmTemplate smTemplate = smTemplateResouces.getTemplateByType(type);
		StringBuffer messageContent = null;
		String expirdMinute = null;
		if(null != smTemplate){
			messageContent = new StringBuffer();
			expirdMinute = smTemplate.getExpireMinute();	
			messageContent.append(smTemplate.getSmTemplateOrgname()+" "+
					smTemplate.getSmTemplateName()+smTemplate.getSmTemplateContent());
		}
		String content = String.format(messageContent.toString(), verifycode,expirdMinute);
		//发送短信
		SendMessageDto result = smsService.sendverificationCode(telephone,content);
		//设置messageLog值
		messageLog.setMessageContent(content);
		messageLog.setCreateDate(new Date());
		messageLog.setMessageType(type);
		messageLog.setVerifycode(verifycode);
		messageLog.setVerifycodeId(idGenerator.next());
		messageLog.setDetail(result.getMessage());
		messageLog.setTelephone(telephone);
		messageLog.setExpireDate(smsService.calculatMessageExpiredDate(expirdMinute));
		if(null != result){
			String status = result.getReturnstatus();
			if("Success".equals(status)){
				messageLog.setSuccess(true);
				map.put("flag", "success");
				map.put("message", "发送成功!");
			}else if("Faild".equals(status)){
				messageLog.setSuccess(false);
				map.put("flag", "fail");
				map.put("message", "发送失败!");
			}
		}
		messageLogResources.save(messageLog);
		return map;
	}
	
}
