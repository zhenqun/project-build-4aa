package com.ido85.party.aaaa.mgmt.security.services.common.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.ido85.party.aaaa.mgmt.security.authentication.application.UserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.InAddSecurityUserDto;
import com.ido85.party.aaaa.mgmt.security.services.common.CommonApi;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import com.ido85.party.aaaa.mgmt.service.RealnameAuthenticaitonClient;

/**
 * Created by fire on 2017/2/22.
 */
@Named
public class CommonApiImpl implements CommonApi {
	@Inject
	private UserApplication userApp;
	@Value("${queryUserInfoByHashUrl}")
	private String queryUserInfoByHashUrl;
	@Value("${checkUserExistUrl}")
	private String checkUserExistUrl;
	@Inject
	private RealnameAuthenticaitonClient realnameAuthenticaitonClient;
//	@Override
//	public Map<String, String> authenticatedUser(InAddSecurityUserDto userDto, User user) {
//		Map<String, String> result = new HashMap<>();
//
//		String hash = StringUtils.getUserNameIDHash(userDto.getRelName(),userDto.getIdCard());
//		User u = userApp.getUserByHash(hash);
//		if(null != u){
//			result.put("flag", "fail");
//			result.put("message", "账号已存在!身份证号:"+userDto.getIdCard()+",姓名:"+userDto.getRelName());
//			return result;
//		}
//		boolean flag = realnameAuthenticaitonClient.realnameAuthentication(userDto.getIdCard(), userDto.getRelName());
//		if(!flag){
//			result.put("flag", "fail");
//			result.put("message", "实名认证失败!");
//			return result;
//		}
//		return result;
//	}
	
	private boolean CheckUserExistFromSimple(String hash) {
		RestTemplate restTemplate = new RestTemplate();
		String url = checkUserExistUrl+hash+"/";
		ResponseEntity<Object> result = restTemplate.getForEntity(url, Object.class);
		if(null == result){
			return false;
		}
		String body = StringUtils.toString(result.getBody());
		if(!StringUtils.isBlank(body) && "true".equals(body)){
			return true;
		}
		return false;
	}
}
