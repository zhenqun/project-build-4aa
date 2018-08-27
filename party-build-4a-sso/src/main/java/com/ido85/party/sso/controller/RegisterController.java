package com.ido85.party.sso.controller;

import com.ido85.party.sso.distribute.generator.IdGenerator;
import com.ido85.party.sso.dto.AuthentiInfoParam;
import com.ido85.party.sso.dto.InBaseUserDto;
import com.ido85.party.sso.dto.RealnameAuthenticationParam;
import com.ido85.party.sso.dto.RoleDto;
import com.ido85.party.sso.log.application.PersonLoginLogApplication;
import com.ido85.party.sso.log.constant.PersonLogConstant;
import com.ido85.party.sso.platform.data.datasource.TargetDataSource;
import com.ido85.party.sso.security.authentication.application.AuthenticationApplication;
import com.ido85.party.sso.security.authentication.application.UserApplication;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.domain.UserRole;
import com.ido85.party.sso.security.authentication.repository.UserRoleResource;
import com.ido85.party.sso.security.utils.ListUntils;
import com.ido85.party.sso.security.utils.StringReplaceUtils;
import com.ido85.party.sso.security.utils.StringUtils;
import com.ido85.party.sso.service.SmClient;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class RegisterController {

	@Inject
	private UserApplication userApp;
	
	@Inject
	private SmClient smClient;
	
	@Inject
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
//	@Inject
//	private UserClient userClient;
	
	@Inject
	private AuthenticationApplication authentication;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Inject
	private PasswordEncoder encoder;
	
	@Inject
	private UserRoleResource userRoleResource;

	@Inject
	private PersonLoginLogApplication personLoginLogApplication;

	@RequestMapping(path="/internet/register", method=RequestMethod.POST)
	@TargetDataSource(name="write")
	public Map<String, Object> register(@RequestBody @Valid InBaseUserDto dto,HttpServletRequest request) throws Exception{
		Map<String, Object> param = new HashMap<>();
		String idCard = dto.getIdCard().trim();
		String password = dto.getPassword();
		boolean checkFlag=false;
		checkFlag = StringUtils.validatePassword(password);
		if (!checkFlag) {
			param.put("flag", "fail");
			param.put("message", "密码强度较弱，请混合使用字母和数字，避开常用密码");
			return param;
		}
		List<String> uniqueKeys = new ArrayList<>();
		if (StringUtils.isNotBlank(idCard)) {
			idCard = idCard.toUpperCase();
		}
		param = smClient.isVerifyCodeValid(dto.getTelePhone(), "2000", dto.getVerifyCode());
		if(param.containsKey("flag") && param.get("flag").equals("fail")){
			return param;
		}else if(param.get("flag").equals("success")){
			String telephone = dto.getTelePhone();
			String name = dto.getName().trim();
			uniqueKeys = dto.getUniqueKey();
			if(ListUntils.isNull(uniqueKeys)){
				uniqueKeys = new ArrayList<>();
				uniqueKeys.add(idCard);
			}
			User user = userApp.getUserByTelephone(telephone);
			if(null != user){
				param.put("flag", "fail");
				param.put("message", "该手机号已经注册!");
				return param;
			}
			User user2 = userApp.getUserByIdcardhash(idCard,name);
			if(null != user2){
				param.put("flag", "fail");
				param.put("message", "该身份证号已经注册!");
				return param;
			}
			//调用认证服务 如果不存在任何认证 则注册失败
			List<RoleDto> roleList = authentication.authentiInfoQuery(uniqueKeys,name,idCard);
			if(ListUntils.isNull(roleList)){
				param.put("flag", "fail");
				param.put("message", "系统暂时无法确认您的身份，请联系所在党组织进行身份确认");
				return param;
			}
			List<String> roleIds = new ArrayList<>();
			for(RoleDto r:roleList){
				roleIds.add(r.getRoleId());
			}
			if(!roleIds.contains("1")){
				//实名认证
				param = authentication.nciicAuthentication(idCard, name);
				if(param.containsKey("flag") && param.get("flag").equals("fail")){
					param.put("flag", "fail");
					param.put("message", "实名认证失败!");
					return param;
				}
			}
			param = userRegister(name,password,telephone,idCard,uniqueKeys,roleList);
			if(param.containsKey("flag") && param.get("flag").equals("fail")){
				return param;
			}
			param.put("flag", "success");
			param.put("message", "注册成功，登录时用户名请填写注册时的手机号码或者身份证号码");
			log.info("============用户注册手机号是:"+telephone+"用户注册身份证号是："+idCard+"用户注册密码是:"+password);
			return param;
		}
		return param;
	}

	@org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, transactionManager = "transactionManagerPrimary")
	private Map<String,Object> userRegister(String name, String password, String telephone, String idCard,List<String> uniqueKeys,List<RoleDto> roleList) throws Exception {
		List<String> roleNames = new ArrayList<String>();
		Map<String,Object> map = new HashMap<>();
		String id = StringUtils.toString(idGenerator.next());
		String hash = StringUtils.getIDHash(idCard.trim());
		String nameIdcardhash = StringUtils.getUserNameIDHash(name.trim(), idCard.trim());
		User newUser = new User();
		newUser.setDisabled(true);
		newUser.setName(StringReplaceUtils.getStarString(name));
		newUser.setPassword(encoder.encode(password));
		newUser.setCreateDate(new Date());
		newUser.setTelePhone(telephone);
		newUser.setAccountExpired(false);
		newUser.setAccountLocked(false);
		newUser.setEnabled(true);
		newUser.setDisabled(false);
		newUser.setPwdExpired(false);
		newUser.setUsername(telephone);//用户账号
		newUser.setHash(nameIdcardhash);
		newUser.setId(id);
		newUser.setIdCardHash(hash);
		AuthentiInfoParam param = new AuthentiInfoParam();
		param.setRelName(name);
		param.setUniqueKey(uniqueKeys);
		List<UserRole> userRoleList = null;
		UserRole userRole = null;
		List<Long> authencationInfoIds = null;
		try {
			if (null != roleList && roleList.size() > 0) {
				userRoleList = new ArrayList<UserRole>();
				authencationInfoIds = new ArrayList<>();
				for (RoleDto roleDto : roleList) {
					if (roleDto.getDelFlag().equals("1")) {
						map.put("flag", "fail");
						map.put("message", roleDto.getUniqueKey() + "已经被认证，不能再次认证!");
						return map;
					}
					roleNames.add(roleDto.getRoleDes());
					userRole = new UserRole();
					userRole.setRelId(idGenerator.next());
					userRole.setRoleId(StringUtils.toLong(roleDto.getRoleId()));
					userRole.setUserId(id);
					userRole.setAuthenticationUserId(roleDto.getUserId());
					userRole.setCreateDate(new Date());
					userRole.setDelFlag("0");
					userRoleList.add(userRole);
					authencationInfoIds.add(roleDto.getAuthencationInfoId());
				}
				userRoleResource.save(userRoleList);
			}
			userApp.insertUser(newUser);
			authentication.updateAuthencationState(authencationInfoIds);
		}catch (JpaSystemException e){
			User user = userApp.getUserByLoginName(telephone,idCard);
			if(null != user){
				map.put("flag", "fail");
				map.put("message", "用户已经存在!");
				return map;
			}else{
				throw e;
			}
		}catch(Exception e){
			throw e;
		}
		// 记录日志
//		registerLogApplication.addRegisterLog(request, username, orgName, orgId,orgCode, name, idCard, userId,"注册失败",LogConstants.REGISTER_SUCCESS,"注册失败");
		map.put("flag", "success");
		//增加日志 调用日志服务
		threadPoolTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					//调用日志服务
					personLoginLogApplication.addRelnameAuthLog(id,hash, PersonLogConstant.RELNAMEAUTH,PersonLogConstant.RELNAMEAUTH);
					personLoginLogApplication.addRoleAuthLog(id,hash,PersonLogConstant.PARTYERAUTH,roleNames,PersonLogConstant.PARTYERAUTH);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		});
		return map;
	}
	
	@RequestMapping(path="/checkValidateCode/{currentCode}/",method=RequestMethod.GET)
	public boolean register(@PathVariable("currentCode")String currentCode,HttpServletRequest request){
		String genCaptcha = (String)request.getSession().getAttribute("validateCode");
		if(genCaptcha.equalsIgnoreCase(currentCode)){
			return true;
		}
		return false;
	}
	
	/**
	 * 实名认证服务
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/authentication/realnameAuthentication",method = RequestMethod.POST)
	private boolean realnameAuthentication(@Valid @RequestBody RealnameAuthenticationParam param) {
		String idCard = param.getIdCard();
		String name = param.getName();
		Map<String,Object> map = authentication.nciicAuthentication(idCard, name);
		if(map.containsKey("flag") && map.get("flag").equals("success")){
			return true;
		}
		return false;
	}
	
	
}
