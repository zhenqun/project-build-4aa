package com.ido85.party.sso.security.authentication.application.impl;

import com.ido85.party.sso.distribute.generator.IdGenerator;
import com.ido85.party.sso.dto.ApplicationCategoryDto;
import com.ido85.party.sso.dto.SendMessageDto;
import com.ido85.party.sso.security.authentication.application.CommonApplication;
import com.ido85.party.sso.security.authentication.domain.Application;
import com.ido85.party.sso.security.authentication.domain.ApplicationCategory;
import com.ido85.party.sso.security.authentication.domain.MessageLog;
import com.ido85.party.sso.security.authentication.domain.SmTemplate;
import com.ido85.party.sso.security.authentication.repository.ApplicationCategoryResources;
import com.ido85.party.sso.security.authentication.repository.MessageLogResources;
import com.ido85.party.sso.security.authentication.repository.SmTemplateResouces;
import com.ido85.party.sso.security.authentication.repository.UserVpnResources;
import com.ido85.party.sso.security.utils.DateUtils;
import com.ido85.party.sso.security.utils.ListUntils;
import com.ido85.party.sso.security.utils.SmsService;
import com.ido85.party.sso.security.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.UnsupportedEncodingException;
import java.util.*;
@Named
public class CommonApplicationImpl implements CommonApplication {

	@Inject
	private MessageLogResources messageLogResources;
	
	@Inject
	private SmTemplateResouces smTemplateResouces;
	
	@Inject
	private SmsService smsService;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Inject
	private ApplicationCategoryResources applicationCategoryResources;
	
	@Inject
	private EntityManager em;
	
	@Inject
	private UserVpnResources userVpnResources;
	
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
			map.put("message", "短信验证码已失效，请重新获取!");
			return map;
		}
		String rightCode = messageLog.getVerifycode();
		if(!rightCode.equals(veifyCode)){
			map.put("flag", "fail");
			map.put("message", "短信验证码错误!");
			return map;
		}
		map.put("flag", "success");
		map.put("message", "短信验证码正确!");
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

	/**
	 * 九宫格应用查询
	 * @throws UnsupportedEncodingException 
	 */
	public List<ApplicationCategoryDto> applicationQuery(String type,String userId) throws UnsupportedEncodingException {
		if(StringUtils.isNull(type)){
			return null;
		}
		//查询该用户所在节点
		String ou = userVpnResources.getOuByByUserId(userId);
		if(StringUtils.isNull(ou)){
			return null;
		}
		List<ApplicationCategoryDto> list = new ArrayList<>();
		List<Application> apps = null;
		ApplicationCategory category = null;
		ApplicationCategoryDto applicationCategoryDto = null;
		StringBuffer sb = new StringBuffer("select a from Application a where a.applicationId in "
				+ "(select a.applicationId from Application a,ApplicationCategory c,ClientUserRel l "
				+ "where a.categoryId = c.categoryId and a.clientId = l.clientId and c.type = :type and l.userId = :userId and a.isDock = 't') or" +
				" a.applicationId in (select a.applicationId from ApplicationCategory c,Application a where a.applicationId = 1)");
		//or a.applicationId in (select a.applicationId from ApplicationCategory c,Application a where "
//		+ "c.categoryId = a.categoryId and a.isDock = 'f' and c.type = :type )
		sb.append(" order by a.order asc");
		Query q = em.createQuery(sb.toString(),Application.class);
		q.setParameter("type", type);
		q.setParameter("userId", userId);
		apps = q.getResultList();
		if(ListUntils.isNotNull(apps)){
			for(Application app:apps){
				if(null != app){
					category = app.getCategory();
					if(null != category){
						applicationCategoryDto = new ApplicationCategoryDto();
						applicationCategoryDto.setApplicationId(StringUtils.toString(app.getApplicationId()));
						applicationCategoryDto.setApplicationImage(app.getApplicationImage());
						applicationCategoryDto.setApplicationName(app.getApplicationName());
						applicationCategoryDto.setApplicationOrder(app.getOrder());
						applicationCategoryDto.setApplicationUrl(app.getApplicationUrl());
						applicationCategoryDto.setCategoryId(StringUtils.toString(category.getCategoryId()));
						applicationCategoryDto.setCategoryName(category.getCategoryName());
						applicationCategoryDto.setCategoryOrder(category.getOrder());
						applicationCategoryDto.setType(category.getType());
						if("1".equals(app.getHaveNode()) && app.getOuName().equals(ou)){
							list.add(applicationCategoryDto);
						}
						if("0".equals(app.getHaveNode())){
							list.add(applicationCategoryDto);
						}
					}
				}
			}
		}
		return list;
	}
	
	
}
