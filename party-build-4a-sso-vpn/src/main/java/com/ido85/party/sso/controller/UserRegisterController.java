package com.ido85.party.sso.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ido85.party.sso.distribute.generator.IdGenerator;
import com.ido85.party.sso.dto.AddAccountDto;
import com.ido85.party.sso.dto.InBaseUserDto;
import com.ido85.party.sso.dto.SimpleUserDto;
import com.ido85.party.sso.security.authentication.application.CommonApplication;
import com.ido85.party.sso.security.authentication.application.RegisterLogApplication;
import com.ido85.party.sso.security.authentication.application.UserApplication;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.domain.UserVpnRel;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.authentication.repository.UserVpnResources;
import com.ido85.party.sso.security.constants.LogConstants;
import com.ido85.party.sso.security.constants.SendMessageConstants;
import com.ido85.party.sso.security.ldap.application.LdapApplication;
import com.ido85.party.sso.security.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
public class UserRegisterController {
	
	@Inject
	private PasswordEncoder encoder;
	
	@Inject
	private UserApplication userApp;
	
	@Inject
	private EntityManager em;
	
	@Inject
	private RegisterLogApplication registerLogApplication;
	
	@Inject
	private UserVpnResources userVpnResources;
	
	public static final String SPRING_SECURITY_FORM_VALIDATE_CODE_KEY = "validateCode";
	private String validateCodeParameter = SPRING_SECURITY_FORM_VALIDATE_CODE_KEY;
	
	@Value("${checkUserExistUrl}")
	private String checkUserExistUrl;
	
	@Value("${queryUserInfoByHashUrl}")
	private String queryUserInfoByHashUrl;
	
	@Inject
	private CommonApplication commonApplication;
	
	@Inject
	private IdGenerator idGenerator;

//	@Value("${LDAP.url}")
//	private String createVPNUrl;
//	
//	@Value("${LDAP.tokenUrl}")
//	private String tokenUrl;
	
	
	@Value("${security.oauth2.server.resourceId}")
	private String RESOURCE_ID;
	
	@Value("${security.oauth2.server.resourceSecret}")
	private String RESOURCE_SECRET;
	
	@Inject
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	@Inject
	private UserResources userResources;
	
	@Inject
	private LdapApplication ldapApplication;
	
	@RequestMapping(path="/acc/register", method=RequestMethod.POST)
	@Transactional
	public Map<String, Object> register(@RequestBody @Valid InBaseUserDto dto,HttpServletRequest request) throws Exception{
		String telephone = dto.getTelePhone();
		String name = dto.getName();
		String idCard = dto.getIdCard();
		String password = dto.getPassword();
		String authorizationCode = dto.getAuthorizationCode();
		String verifyCode = dto.getVerifyCode();
		if (StringUtils.isNotBlank(idCard)) {
			idCard = idCard.toUpperCase();
		}
		String hash = StringUtils.getUserNameIDHash(name,idCard);
		String vpnpwd = dto.getVpnpwd();

		Map<String, Object> param = new HashMap<>();
		boolean checkFlag=false;
		checkFlag = StringUtils.validatePassword(password);
		if (!checkFlag) {
			param.put("flag", "fail");
			param.put("message", "密码强度较弱，请混合使用大小写字母和数字，避开常用密码");
			return param;
		}
//		Zxcvbn zxcvbn = new Zxcvbn();
//		Strength strength = zxcvbn.measure(password);
//		if(null != strength){
//			int score = strength.getScore();
//			if(score<2){
//				param.put("flag", "fail");
//				param.put("message", "密码强度较弱，请混合使用字母和数字，避开常用密码!");
//				return param;
//			}
//		}
		//手机验证码对比 
		//校验验证码
		param = commonApplication.isVerifyCodeValid(telephone,SendMessageConstants.REGISTER,verifyCode);
		if(null != param && param.get("flag").equals("fail")){
			return param;
		}
		User user = userResources.getUserbyTelIdcard(telephone,idCard);
		if(null != user){
//			registerLogApplication.addRegisterLog(request,username,null,null,null,name,idCard,null,"用户已经存在!",LogConstants.REGISTER_FAIL,"注册失败");
			param.put("flag", "fail");
			param.put("message", "身份证号或手机号已经存在!");
			return param;
		}
		//如果生成了账号，则去激活
		//查询是否已经生成了账号
		User u = userApp.getUserByIdcardAndName(name,idCard);
		if(null != u && null != u.getAuthorizationCode() && u.getAuthorizationCode().equals(authorizationCode)){ 
			String userId = u.getId();
			if(u.isActivation()){
//				registerLogApplication.addRegisterLog(request,username,u.getOrgName(),u.getOrgId(),u.getOrgCode(),name,idCard,u.getId(),"系统无法确认党员身份",LogConstants.REGISTER_FAIL,"注册失败");
				param.put("flag", "fail");
				param.put("message", "您的账号已经激活");
				return param;
			}
			// TODO 输入orgId orgName
			userRegister(u,password,telephone,hash);
			//更新vpn密码 TODO
			boolean vpnflag = ldapApplication.updateVpnPwd(u.getId(),vpnpwd);
			if(vpnflag){
				param.put("flag", "success");
				param.put("message", "激活成功!");
			}else{
				param.put("flag", "success");
				param.put("message", "激活成功!VPN密码修改失败!");
			}
		}else{
//			registerLogApplication.addRegisterLog(request,username,orgName,orgId,orgCode,name,idCard,userId,"系统未分配账号或者授权码错误",LogConstants.REGISTER_FAIL,"注册失败");
			param.put("flag", "suggest");
			param.put("message", "平台未为您分配管理员账号或者您的授权码错误，请仔细核对填写信息或者联系所在党组织进行开通");
			return param;
		}
		return param;
	}
	
	
	@Inject
	@LoadBalanced
	private RestTemplate restTemplate;
	
//	/**
//	 * 生成正式的VPN账号并发送短信
//	 * @param usernamey
//	 * @param password
//	 * @param idCard
//	 * @param orgId
//	 */
//	private void createVPN(String userId, String password, String idCard, String orgId, String telephone,String ou) {
//		//生成token
//		String token = getToken();
//		AddAccountDto dto = new AddAccountDto();
//		dto.setIdCard(idCard);
//		dto.setOrgId(orgId);
//		dto.setPassword(password);
////		dto.setUsername(username);
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Authorization", "bearer "+token);
//		headers.add("Content-Type", "application/json");
//		HttpEntity<AddAccountDto> formEntity = new HttpEntity<>(dto, headers);
//		boolean flag = restTemplate.exchange("http://party-build-4a-sync/account/saveAccount", HttpMethod.POST, formEntity, boolean.class,dto).getBody();
////		//发送短信
//		if(flag){
//			//保存用户vpn账号关系
//			UserVpnRel rel = new UserVpnRel();
//			rel.setCreateDate(new Date());
//			rel.setOu(ou);
//			rel.setUserId(userId);
//			rel.setUserVpnId(idGenerator.next());
//			rel.setVpn(idCard);
//			userVpnResources.save(rel);
//			commonApplication.sendSimpleMessage("2004",telephone);
//		}
//	}
//
//	/**
//	 * 获取token
//	 * @return
//	 */
//	private String getToken() {
//		RestTemplate restTemplate = new RestTemplate();
//		HttpHeaders headers = new HttpHeaders();
//		String key  = RESOURCE_ID+":"+RESOURCE_SECRET;
//		String authorization = Base64.getEncoder().encodeToString(key.getBytes());
//		headers.add("Authorization",
//				"Basic "+ authorization);
//		HttpEntity<String> formEntity = new HttpEntity<>("", headers);
//		@SuppressWarnings("unchecked")
//		Map<String,String> o = restTemplate.exchange(tokenUrl, HttpMethod.POST,formEntity,Map.class).getBody();
//		if(null != o){
//			return o.get("access_token").toString();
//		}else{
//			return null;
//		}
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

	/**
	 * 完善信息并将状态改为激活
	 * @param id
	 * @param password
	 * @param telephone
	 * @param username
	 * @param object
	 * @param object2
	 */
	private void userRegister(User u, String password, String telephone, String hash) {
		u.setDisabled(false);
		u.setPassword(encoder.encode(password));
		u.setActivationDate(new Date());
		u.setTelePhone(telephone);
		u.setActivation(true);
		u.setUsername(u.getIdCard());//用户账号
		u.setAccountExpired(false);
		u.setAccountLocked(false);
		u.setDisabled(false);
		u.setEnabled(true);
//		u.setHash(hash);
		u.setNeedCa(false);
		u.setPwdExpired(false);
		u.setTelePhone(telephone);
//		RestTemplate restTemplate = new RestTemplate();
//		String url = queryUserInfoByHashUrl;
//		List<String> params = new ArrayList<String>();
//		params.add(hash);
//		SimpleUserDto[] simpleUserDtoArr = restTemplate.postForObject(url, params, SimpleUserDto[].class);
//		SimpleUserDto simpleUserDto = null;
//		String orgName = null;
//		String orgId = null;
//		String userId = null;
//		String orgCode = null;
//		if(null != simpleUserDtoArr){
//			simpleUserDto = simpleUserDtoArr[0];
//			orgName = simpleUserDto.getOrgName();
//			orgId = simpleUserDto.getOrgId();
//			userId = simpleUserDto.getUserId();
//			u.setId(userId);
////			u.setOrgId(orgId);
////			u.setOrgName(orgName);
////			u.setOrgCode(orgCode);
//		}
		userResources.save(u);
	}


	protected String obtainValidateCode(HttpServletRequest request) {
		return request.getParameter(validateCodeParameter);
	}
	
	@RequestMapping(path="/checkValidateCode/{currentCode}/",method=RequestMethod.GET)
	public boolean checkValidateCode(@PathVariable("currentCode")String currentCode,HttpServletRequest request){
		String genCaptcha = (String)request.getSession().getAttribute("validateCode");
		if(genCaptcha.equalsIgnoreCase(currentCode)){
			return true;
		}
		return false;
	}
}
