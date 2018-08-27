/**
 * 
 */
package com.ido85.party.aaaa.mgmt.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.Valid;

import com.ido85.party.aaaa.mgmt.dto.*;
import com.ido85.party.aaaa.mgmt.internet.resource.InternetUserResources;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.constants.SendMessageConstants;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ido85.party.aaaa.mgmt.dto.base.OutBasePageDto;
import com.ido85.party.aaaa.mgmt.internet.application.InternetUserApplication;
import com.ido85.party.aaaa.mgmt.internet.domain.InternetUser;
import com.ido85.party.aaaa.mgmt.internet.dto.ApplicationDto;
import com.ido85.party.aaaa.mgmt.internet.dto.IntegerUserDto;
import com.ido85.party.aaaa.mgmt.internet.dto.IntegerUserQueryParam;
import com.ido85.party.aaaa.mgmt.security.authentication.application.CommonApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.UserApplication;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import org.springframework.web.client.RestTemplate;


/**
 * 
 * @author rongxj
 *
 */
@RestController
@Slf4j
public class InternetController {

	@Inject
	private InternetUserApplication userApplication;

	@Inject
	private PasswordEncoder encoder;

	@Value("${security.defaultPassword}")
	private String defaultPassword;

	@Value("${checkUserUrl}")
	private String checkUserUrl;

	@Value("${sendHashurl}")
	private String sendHashurl;

	@Value("${queryUserInfoByHashUrl}")
	private String queryUserInfoByHashUrl;

	@Inject
	private InternetUserResources internetUserResources;
	@Inject
	private CommonApplication commonApplication;


	/**
	 * 网站会员查询
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(path = "/manage/internet/internetUserQuery", method = {RequestMethod.POST})
	@ResponseBody
	public OutBasePageDto<IntegerUserDto> internetUserQuery(@RequestBody IntegerUserQueryParam param) {
		if (null == param.getPageNo() || param.getPageNo().intValue() <= 0) {
			param.setPageNo(1);
		}
		if (null == param.getPageSize()) {
			param.setPageSize(20);
		}
		List<IntegerUserDto> userlist = userApplication.getInternetUserByCondition(param);
		Long cnt = userApplication.getInternetUserCntByCondition(param);
		OutBasePageDto<IntegerUserDto> page = new OutBasePageDto<IntegerUserDto>();
		page.setCount(cnt);
		page.setData(userlist);
		page.setPageNo(param.getPageNo());
		page.setPageSize(param.getPageSize());
		return page;
	}

	/**
	 * 网站会员状态修改
	 *
	 * @param status
	 * @param userId
	 * @return
	 */
	@RequestMapping(path = "/manage/internet/modifyInternetUserStatus/{userId}/{status}", method = {RequestMethod.GET})
	@ResponseBody
	public boolean modifyInternetUserStatus(@PathVariable("status") Integer status, @PathVariable("userId") Long userId) {
		userId = StringUtils.toLong(userId);
		status = StringUtils.toInteger(status);
		boolean flag = userApplication.modifyInternetUserStatus(userId, status);
		return flag;
	}

	/**
	 * 网站会员应用分配
	 *
	 * @param status
	 * @param userId
	 * @return
	 */
	@RequestMapping(path = "/manage/internet/allotInternetUserApp", method = {RequestMethod.POST})
	@ResponseBody
	public boolean allotInternetUserApp(@Valid @RequestBody ApplicationAllotParam param) {
		boolean flag = userApplication.allotInternetUserApp(param);
		return flag;
	}

	/**
	 * 查询应用列表
	 *
	 * @param status
	 * @param userId
	 * @return
	 */
	@RequestMapping(path = "/manage/internet/internetApplicationQuery", method = {RequestMethod.POST})
	@ResponseBody
	public List<ApplicationDto> applicationQuery() {
		List<ApplicationDto> list = userApplication.applicationQuery();
		return list;
	}

	/**
	 * 重置密码
	 *
	 * @param username
	 * @param newPassword
	 * @return
	 */
	@RequestMapping(path = "/manage/resetInternetUserPwd/{userId}", method = RequestMethod.GET)
	public boolean resetInternetUserPwd(@PathVariable("userId") Long userId) {
		InternetUser user = userApplication.getInternetUserById(userId);
		if (null == user) {
			return false;
		}
		user.setPassword(encoder.encode(defaultPassword));
		userApplication.saveInternetUser(user);
		return true;
	}


	/**
	 * 管理员注册手机号修改
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(path = "/expand/resetTelePhoneByAdmin", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> resetTelePhoneByAdmin(@Valid @RequestBody ResetTelePhoneDto param) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapCode = new HashMap<String, Object>();
		String oldTele = null;
		String newTele = null;
		String idCard = null;
		String name = null;
		boolean flag = false;
		if (null != param) {
			oldTele = param.getOldTelephone().trim();
			newTele = param.getNewTelephone().trim();
			idCard = param.getIdCard().trim();
			name = param.getRelName().trim();
			String hash = StringUtils.getUserNameIDHash(name, idCard);
			InternetUser u = internetUserResources.getUserByIdcardHash(hash);
			if (null != u) {
				if (oldTele.equals(newTele)) {
					map.put("flag", "fail");
					map.put("message", "两次输入的手机号相同");
					return map;
				}
				if (!u.getTelePhone().equals(oldTele)) {
					map.put("flag", "fail");
					map.put("message", "该党员旧手机号不正确");
					return map;
				}
				if (u.getUsername().equals(newTele)) {
					map.put("flag", "fail");
					map.put("message", "新手机号与旧手机号不能相同");
					return map;
				}
				if (!u.getIdCardHash().equals(StringUtils.getIDHash(idCard))) {
					map.put("flag", "fail");
					map.put("message", "该党员身份证号与输入不匹配");
					return map;
				}
				if (!u.getHash().equals(hash)) {
					map.put("flag", "fail");
					map.put("message", "该党员真实姓名身份证号与输入不匹配");
					return map;
				}

				InternetUser uu = internetUserResources.getUserByPhone(newTele);
				if (null != uu) {
					map.put("flag", "fail");
					map.put("message", "新手机号已存在");
					return map;
				}

				// TODO 第一书记
				flag = this.CheckUserExistFromSimple(hash);
				if (!flag) {
					map.put("flag", "fail");
					map.put("message", "输入的人员不是党员身份");
					return map;
				}

				//发送短信并进行更新操作同时记录日志
				mapCode = commonApplication.isSSOVerifyCodeValid(newTele, SendMessageConstants.NEWPHONEVERIFY, param.getVerifyCode());
				if(mapCode.containsKey("flag") && mapCode.get("flag").equals("fail")){
					map.put("flag","fail");
					map.put("message",mapCode.get("message"));
					return map;
				}
				userApplication.addUpdateUserTeleLog(oldTele, newTele, name, idCard, hash,
						param.getHash(), param.getUpdateBy(), param.getUpdateName(), param.getVerifyCode());
				/**
				 * 同步简向库更新手机号
				 */
				this.updateSycnSimple(name, idCard);

				map.put("flag", "success");
				map.put("message", "修改成功");
				return map;
			} else {
				map.put("flag", "fail");
				map.put("message", "该党员旧手机号不正确");
				return map;
			}
		}
		return map;
	}


	public boolean updateSycnSimple(String name, String idCard) {

		RestTemplate restTemplate = new RestTemplate();
		boolean flag = false;
		List<Object[]> newList = new ArrayList<>();
		String hash = StringUtils.getUserNameIDHash(name, idCard);

		newList = userApplication.updateTeleByAdminToSimple(hash);
		if (ListUntils.isNotNull(newList)) {
			HttpEntity entity = new HttpEntity(newList);
			flag = restTemplate.exchange(sendHashurl, HttpMethod.POST, entity, boolean.class).getBody();
		}
		if (!flag) {
			log.error("===============更新后数据通信错误，请联系管理员！");
			return false;
		}
		return flag;

	}

//	/**
//	 * 判断是否是党员或者是第一书记角色
//	 * @param param
//	 * @return
//	 */
//	@RequestMapping(path="/checkIsParty",method=RequestMethod.POST)
//	@ResponseBody
//	public boolean checkIsParty(@Valid @RequestBody InCheckIsPartyDto param)  throws  Exception{
//		String hash=null;
//		String name=null;
//		String idCard=null;
//		boolean flag=false;
//		if(null!=param){
//			name=param.getRelName();
//			idCard=param.getIdCard();
//			hash=StringUtils.getUserNameIDHash(name,idCard);
//			flag= this.CheckUserExistFromSimple(hash);
//			if(flag){
//				return true;
//			}else{
//				return false;
//			}
//		}
//		return false;
//
//	}

	/**
	 * 调用简向库查询是否实名认证备案的党员
	 *
	 * @param hash
	 * @return
	 */
	public boolean CheckUserExistFromSimple(String hash) {

		RestTemplate restTemplate = new RestTemplate();

		boolean receiveValue = false;

		List<String> listHash = new ArrayList<>();

		listHash.add(hash);

		SimpleUserDto[] simpleUserDtos = null;
		try {
			simpleUserDtos = restTemplate.postForObject(queryUserInfoByHashUrl, listHash, SimpleUserDto[].class);
			if (null != simpleUserDtos) {
				receiveValue = true;
			} else {
				receiveValue = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return receiveValue;
		}
		return receiveValue;
	}

}


