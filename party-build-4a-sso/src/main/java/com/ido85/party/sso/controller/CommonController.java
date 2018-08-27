package com.ido85.party.sso.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ido85.party.sso.dto.SendMessageParam;
import com.ido85.party.sso.service.SmClient;

@RestController
public class CommonController {

	@Inject
	private SmClient smClient;
	
	@Value("${sendMessage.intervaltime}")
	private String intervaltime;
	
	/**
	 * 发送短信
	 * @param orgId
	 * @return
	 */
	@RequestMapping(path="/sendMessage", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> sendMessage(@Valid @RequestBody SendMessageParam param){
		Map<String,Object> map = new HashMap<String,Object>();
		map = smClient.sendMessage(param);
		return map;
	}

	@RequestMapping(path="/cologin",method=RequestMethod.GET)
	public void revokeToken(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sessionId = request.getParameter("sessionId");
//			String JSESSIONID = request.getSession().getId();//获取当前JSESSIONID （不管是从主域还是二级域访问产生）

		Cookie cookie = new Cookie("SSO-SID", sessionId);
//			cookie.setDomain(""); //关键在这里，将cookie设成主域名访问，确保不同域之间都能获取到该cookie的值，从而确保session统一
		cookie.setPath("/");
		response.addCookie(cookie);  //将cookie返回到客户端
		request.getRequestDispatcher("/register.html").forward(request,response);

	}
	
}
