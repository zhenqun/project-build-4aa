package com.ido85.party.sso.security.authentication.application;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.ido85.party.sso.dto.ApplicationCategoryDto;
import com.ido85.party.sso.dto.ApplicationDto;

public interface CommonApplication {

	/**
	 * 校验手机验证码
	 * @param telephone
	 * @param register
	 * @return
	 */
	Map<String, Object> isVerifyCodeValid(String telephone, String type,String veifyCode);

	/**
	 * 发送非验证码短信
	 * @param string
	 * @param telephone
	 */
	Map<String, Object> sendSimpleMessage(String string, String telephone);

	/**
	 * 九宫格应用查询
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	List<ApplicationCategoryDto> applicationQuery(String type,String userId) throws UnsupportedEncodingException;

}
