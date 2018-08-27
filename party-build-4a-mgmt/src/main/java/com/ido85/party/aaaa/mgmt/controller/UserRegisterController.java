package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.dto.InBaseUserDto;
import com.ido85.party.aaaa.mgmt.security.authentication.application.CommonApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.RegisterLogApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.UserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserVpnRel;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserResources;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserVpnRelResources;
import com.ido85.party.aaaa.mgmt.security.constants.SendMessageConstants;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



@RestController
public class UserRegisterController {
	
	@Inject
	private PasswordEncoder encoder;
	
	@Inject
	private UserApplication userApp;
	
	@PersistenceContext(unitName = "admin")
	private EntityManager em;
	
	@Inject
	private RegisterLogApplication registerLogApplication;
	
	public static final String SPRING_SECURITY_FORM_VALIDATE_CODE_KEY = "validateCode";
	private String validateCodeParameter = SPRING_SECURITY_FORM_VALIDATE_CODE_KEY;
	
	@Value("${checkUserExistUrl}")
	private String checkUserExistUrl;
	
	@Value("${queryUserInfoByHashUrl}")
	private String queryUserInfoByHashUrl;
	
	@Inject
	private CommonApplication commonApplication;
	
	@Inject
	private UserResources userResources;
	
	@Inject
	private UserVpnRelResources userVpnRelResources;
	
	@RequestMapping(path="/user/register", method=RequestMethod.POST)
	@Transactional
	public Map<String, Object> register(@RequestBody @Valid InBaseUserDto dto,HttpServletRequest request) throws Exception{
		String telephone = dto.getTelePhone();
		String name = dto.getName();
		String idCard = dto.getIdCard();
		String password = dto.getPassword();
		String authorizationCode = dto.getAuthorizationCode();
		String verifyCode = dto.getVerifyCode();
		String vpnpwd = dto.getVpnpwd();
		if (StringUtils.isNotBlank(idCard)) {
			idCard = idCard.toUpperCase();
		}

		String hash = StringUtils.getUserNameIDHash(name,idCard);
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
		//校验验证码
		param = commonApplication.isVerifyCodeValid(telephone,SendMessageConstants.REGISTER,verifyCode);
		if(null != param && param.get("flag").equals("fail")){
			return param;
		}
		User user = userResources.getUserByIdCardTelphone(idCard,telephone);
		if(null != user){
//			registerLogApplication.addRegisterLog(request,username,null,null,null,name,idCard,null,"用户已经存在!",LogConstants.REGISTER_FAIL,"注册失败");
			param.put("flag", "fail");
			param.put("message", "身份证号或手机号已经存在!");
			return param;
		}
		//如果生成了账号，则去激活
		//查询是否已经生成了账号
		User u = userApp.getUserByIdcardAndName(idCard);
		if(null != u && null != u.getAuthorizationCode() && u.getAuthorizationCode().equals(authorizationCode)){ 
			if(u.isActivation()){
//				registerLogApplication.addRegisterLog(request,username,u.getOrgName(),u.getOrgId(),u.getOrgCode(),name,idCard,u.getId(),"系统无法确认党员身份",LogConstants.REGISTER_FAIL,"注册失败");
				param.put("flag", "fail");
				param.put("message", "您的账号已经激活");
				return param;
			}
			// TODO 输入orgId orgName
			userRegister(u,password,telephone,hash,idCard);
			//获取用户的vpn账号以及ou
			UserVpnRel rel = userVpnRelResources.getRelByUserId(u.getId());
			boolean flag = commonApplication.updateVpnPwd(rel.getVpn(),rel.getOuName(),vpnpwd);
			if(flag){
				param.put("flag", "success");
				param.put("message", "激活成功!");
			}
//			registerLogApplication.addRegisterLog(request, username, u.getOrgName(), u.getOrgId(), u.getOrgCode(),name, idCard, u.getId(),"注册成功",LogConstants.REGISTER_SUCCESS,"注册成功");
			//手机号和党员一一绑定
		}else{
//			registerLogApplication.addRegisterLog(request,username,orgName,orgId,name,orgCode,idCard,userId,"系统未分配账号或者授权码错误",LogConstants.REGISTER_FAIL,"注册失败");
			param.put("flag", "suggest");
			param.put("message", "平台未为您分配管理员账号或者您的授权码错误，请联系所在党组织进行身份认证开通");
			return param;
		}
		return param;
	}
	
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
	 * @param password
	 * @param telephone
	 */
	private void userRegister(User u, String password, String telephone, String hash,String idCard) {
		u.setDisabled(false);
		u.setPassword(encoder.encode(password));
		u.setActivationDate(new Date());
		u.setTelePhone(telephone);
		u.setActivation(true);
		u.setUsername(idCard);//用户账号
		u.setAccountExpired(false);
		u.setAccountLocked(false);
		u.setDisabled(false);
		u.setEnabled(true);
		u.setPwdExpired(false);
//		RestTemplate restTemplate = new RestTemplate();
//		String url = queryUserInfoByHashUrl;
//		List<String> params = new ArrayList<String>();
//		params.add(hash);
//		SimpleUserDto[] simpleUserDtoArr = restTemplate.postForObject(url, params, SimpleUserDto[].class);
//		SimpleUserDto simpleUserDto = null;
//		if(null == simpleUserDtoArr){
//			
//		}
		em.merge(u);
	}


	protected String obtainValidateCode(HttpServletRequest request) {
		return request.getParameter(validateCodeParameter);
	}
	
	@RequestMapping(path="/checkValidateCode/{currentCode}/",method=RequestMethod.GET)
	public boolean register(@PathVariable("currentCode")String currentCode,HttpServletRequest request){
		String genCaptcha = (String)request.getSession().getAttribute("validateCode");
		if(genCaptcha.equalsIgnoreCase(currentCode)){
			return true;
		}
		return false;
	}
}
