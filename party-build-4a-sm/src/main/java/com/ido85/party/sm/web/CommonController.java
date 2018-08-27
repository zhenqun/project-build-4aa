package com.ido85.party.sm.web;

import com.ido85.party.platform.distribute.generator.IdGenerator;
import com.ido85.party.platform.utils.StringUtils;
import com.ido85.party.sm.application.CommonApplication;
import com.ido85.party.sm.constant.SendMessageConstants;
import com.ido85.party.sm.domain.MessageLog;
import com.ido85.party.sm.domain.SmTemplate;
import com.ido85.party.sm.dto.SendMessageDto;
import com.ido85.party.sm.dto.SendMessageParam;
import com.ido85.party.sm.dto.VerifyCodeParam;
import com.ido85.party.sm.dto.VerifyRtPwdVfCodeParam;
import com.ido85.party.sm.resource.MessageLogResources;
import com.ido85.party.sm.resource.SmTemplateResouces;
import com.ido85.party.sm.service.SmsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CommonController {
	
	@Inject
	private SmTemplateResouces smTemplateResouces;
	
	@Inject
	private MessageLogResources messageLogResources;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Inject
	private SmsService smsService;
	
	@Value("${sendMessage.intervaltime}")
	private String intervaltime;
	
	@Inject
	private CommonApplication commonApplication;

	@Value("${sendMessage.maxcnt}")
	private String maxcnt;

	/**
	 * 发送短信
	 * @return
	 */
	@RequestMapping(path="/sendMessage", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> sendMessage(@Valid @RequestBody SendMessageParam param){
		Map<String,String> map = new HashMap<String,String>();
		String telephone = param.getTelephone();
		//检测手机号今天发送次数
		Long count = smsService.getTelSendCount(telephone);
		if(count.longValue()>=Long.parseLong(maxcnt)){
			map.put("flag", "fail");
			map.put("message", "该手机号已达到发送最大次数，请明天再试!");
			return map;
		}
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
//		SendMessageDto result = new SendMessageDto();
//		result.setReturnstatus("Success");
//		result.setMessage("压力测试");
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
	
	/**
	 * 校验手机验证码
	 */
	@RequestMapping(path="/isVerifyCodeValid", method={RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> isVerifyCodeValid(@Valid @RequestBody VerifyCodeParam param) {
		Map<String, Object> map = commonApplication.isVerifyCodeValid(param.getTelephone(), 
				param.getType(), param.getVeifyCode());
		return map;
	}
	
	/**
	 * 验证找回密码短信验证码是否正确
	 */
	@RequestMapping(path="/internet/verifyRetrievePwdVerificationCode",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> verifyRetrievePwdVerificationCode(@Valid @RequestBody VerifyRtPwdVfCodeParam param){
		Map<String, Object> map = new HashMap<String,Object>();
		map = commonApplication.isVerifyCodeValid(param.getTelePhone(),SendMessageConstants.RETRIEVEPASSWORD,param.getVerificationCode());
		return map;
	}


	/**
	 * 发送短信for 节点区
	 * @return
	 */
	@RequestMapping(path="/sendMessageForvpn", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> sendMessageForvpn(@Valid @RequestBody SendMessageParam param){
		Map<String,String> map = new HashMap<String,String>();
		String telephone = param.getTelephone();
		//检测手机号今天发送次数
		Long count = smsService.getTelSendCount(telephone);
		if(count.longValue()>=Long.parseLong(maxcnt)){
			map.put("flag", "fail");
			map.put("message", "该手机号已达到发送最大次数，请明天再试!");
			return map;
		}
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
//		SendMessageDto result = new SendMessageDto();
//		result.setReturnstatus("Success");
//		result.setMessage("压力测试");
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
