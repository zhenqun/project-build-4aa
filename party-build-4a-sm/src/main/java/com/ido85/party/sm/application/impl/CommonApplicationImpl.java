package com.ido85.party.sm.application.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.ido85.party.platform.distribute.generator.IdGenerator;
import com.ido85.party.platform.utils.DateUtils;
import com.ido85.party.platform.utils.ListUntils;
import com.ido85.party.sm.application.CommonApplication;
import com.ido85.party.sm.domain.MessageLog;
import com.ido85.party.sm.domain.SmTemplate;
import com.ido85.party.sm.dto.SendMessageDto;
import com.ido85.party.sm.resource.MessageLogResources;
import com.ido85.party.sm.resource.SmTemplateResouces;
import com.ido85.party.sm.service.SmsService;

@Named
public class CommonApplicationImpl implements CommonApplication {

	@Inject
	private MessageLogResources messageLogResources;
	
	@Inject
	private SmsService smsService;
	
	@Inject
	private SmTemplateResouces smTemplateResouces;
	
	@Inject
	private IdGenerator idGenerator;
	
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
	public Map<String, Object> sendSimpleMessage(String type, String telephone) {
		Map<String,Object> map = new HashMap<String,Object>();
		MessageLog messageLog = new MessageLog();
		//查模板
		SmTemplate smTemplate = smTemplateResouces.getTemplateByType(type);
		StringBuffer messageContent = null;
		if(null != smTemplate){
			messageContent = new StringBuffer();
			messageContent.append(smTemplate.getSmTemplateOrgname()+" "+
					smTemplate.getSmTemplateName()+smTemplate.getSmTemplateContent());
		}
		String content = messageContent.toString();
		//发送短信
		SendMessageDto result = smsService.sendverificationCode(telephone,content);
		//设置messageLog值
		messageLog.setMessageContent(content);
		messageLog.setCreateDate(new Date());
		messageLog.setMessageType(type);
		messageLog.setVerifycode("000000");
		messageLog.setVerifycodeId(idGenerator.next());
		messageLog.setDetail(result.getMessage());
		messageLog.setTelephone(telephone);
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
