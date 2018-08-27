package com.ido85.party.sm.application;

import java.util.Map;

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

}
