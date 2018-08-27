package com.ido85.party.sm.service;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ido85.party.platform.utils.DateUtils;
import com.ido85.party.platform.utils.MD5Utils;
import com.ido85.party.platform.utils.StringUtils;
import com.ido85.party.sm.application.MessageLogApplication;
import com.ido85.party.sm.domain.MessageLog;
import com.ido85.party.sm.dto.SendMessageDto;

@Named
public class SmsService {
	
	@Value("${sendMessage.url}")
	private String url;
	
	@Value("${sendMessage.userpwd}")
	private String userpwd;
	
	@Value("${sendMessage.account}")
	private String account;
	
	@Value("${sendMessage.intervaltime}")
	private String intervaltime;
	
	@Inject
	private MessageLogApplication messageLogApplication;
	
	public SendMessageDto sendverificationCode(String mobile,String content){
		SendMessageDto entity = null;
		if(!StringUtils.isNull(mobile)){
			RestTemplate restTemplate = new RestTemplate();
			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("userid", null);
			map.add("account", account);
			map.add("password", MD5Utils.getMd5(userpwd));
			map.add("mobile", mobile);
			map.add("content", content);
			map.add("sendTime", null);
			map.add("action", "send");
			map.add("extno", null);
			entity = restTemplate.postForObject(url, map, SendMessageDto.class);
		}
		return entity;
	}

	/**
	 * 计算验证码有效期
	 * @param expirdMinute
	 * @return
	 */
	public Date calculatMessageExpiredDate(String expirdMinute) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(new Date());  
		cal.add(Calendar.MINUTE, Integer.parseInt(expirdMinute));  
		return cal.getTime(); 
	}
	
	/**
	 * 是否连续发送
	 * @param mobile
	 * @return
	 */
	public boolean isContinuitySend(String mobile) {
		MessageLog messageLog = messageLogApplication.getLastMessageByMobile(mobile);
		if(null == messageLog){
			return false;
		}
		//判断时间
		Date date = messageLog.getCreateDate();
		if(DateUtils.difference(new Date(),date) <= StringUtils.toInteger(intervaltime)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 查询手机号今天发送总条数
	 * @param telephone
	 * @return
	 */
	public Long getTelSendCount(String telephone) {
		try{
			Date startDate = DateUtils.forDateDelDay(new Date());
			Date endDate = DateUtils.forDateAddDay(new Date());
			Long cnt = messageLogApplication.getTelSendCount(telephone,startDate,endDate);
			return cnt;
		} catch (Exception e){
			return 999999L;
		}
	}
}
