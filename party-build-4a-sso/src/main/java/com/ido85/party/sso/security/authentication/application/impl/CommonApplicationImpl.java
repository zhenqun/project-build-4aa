package com.ido85.party.sso.security.authentication.application.impl;

import com.ido85.party.sso.platform.data.datasource.TargetDataSource;
import com.ido85.party.sso.security.authentication.application.CommonApplication;
import com.ido85.party.sso.security.authentication.domain.MessageLog;
import com.ido85.party.sso.security.authentication.repository.MessageLogResources;
import com.ido85.party.sso.security.oauth2.domain.PlatformClientDetails;
import com.ido85.party.sso.security.oauth2.repository.ClientRepository;
import com.ido85.party.sso.security.utils.DateUtils;
import com.ido85.party.sso.security.utils.ListUntils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Named
public class CommonApplicationImpl implements CommonApplication {

	@Inject
	private MessageLogResources messageLogResources;

	@Inject
	private ClientRepository clientRepository;

	/**
	 * 校验手机验证码
	 */
	public Map<String, Object> isVerifyCodeValid(String telephone, String type,String veifyCode) {
		Map<String, Object> map = new HashMap<String,Object>();
		MessageLog messageLog = null;
		List<MessageLog> list = messageLogResources.getVerifyCodeByTelephoneandType(telephone,type);
		if(ListUntils.isNotNull(list)){
			messageLog = list.get(0);
		}else{
			map.put("flag", "fail");
			map.put("message", "没有手机验证码记录！");
			return map;
		}
		Date expireDate = messageLog.getExpireDate();
		Date now = new Date();
		Long minutes = DateUtils.difference(now,expireDate);
		if(minutes > 0){
			map.put("flag", "fail");
			map.put("message", "验证码已失效，请重新获取!");
			return map;
		}
		String rightCode = messageLog.getVerifycode();
		if(!rightCode.equals(veifyCode)){
			map.put("flag", "fail");
			map.put("message", "验证码错误!");
			return map;
		}
		map.put("flag", "success");
		map.put("message", "验证码正确!");
		return map;
	}

	@Override
	@TargetDataSource(name = "read")
	public PlatformClientDetails getClientByClientId(String clientId) {
		return clientRepository.getClientByClientId(clientId);
	}


}
