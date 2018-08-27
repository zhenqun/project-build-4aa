/**
 * 
 */
package com.ido85.party.sso.controller;

import com.ido85.party.sso.distribute.generator.IdGenerator;
import com.ido85.party.sso.dto.*;
import com.ido85.party.sso.platform.data.datasource.TargetDataSource;
import com.ido85.party.sso.security.authentication.application.ConfigApplication;
import com.ido85.party.sso.security.authentication.application.UpdateUserTeleLogApplication;
import com.ido85.party.sso.security.authentication.application.UserApplication;
import com.ido85.party.sso.security.authentication.domain.AuthenticationInfo;
import com.ido85.party.sso.security.authentication.domain.Role;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.domain.UserRole;
import com.ido85.party.sso.security.authentication.repository.AuthenticationInfoResources;
import com.ido85.party.sso.security.authentication.repository.UpdateUserTeleLogResources;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.authentication.repository.UserRoleResource;
import com.ido85.party.sso.security.constants.ConfigEnum;
import com.ido85.party.sso.security.constants.SendMessageConstants;
import com.ido85.party.sso.security.utils.*;
import com.ido85.party.sso.service.SmClient;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.*;

/**
 * 
 * @author rongxj
 *
 */
@RestController
@Slf4j
public class UserController {

	@Inject
	private UserResources userResource;
	
	@Value("${userLogoUrl}")
	private String userLogoUrl;
	
	@Value("${uploadLogoUrl}")
	private String uploadLogoUrl;
	
	@Value("${defaultUserLogoUrl}")
	private String defaultUserLogoUrl;

	@Value("${checkUserExistUrl}")
	private String checkUserExistUrl;

	@Value("${checkUserUrl}")
	private String checkUserUrl;

	@Value("${sendHashurl}")
	private String sendHashurl;

	@Inject
	private PasswordEncoder encoder;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Inject
	private UserApplication userApp;
	
	@Inject
	private SmClient smClient;
	
	@Inject
	private ConfigApplication configApp;

	@Inject
	private AuthenticationInfoResources authenticationInfoResources;

	@Inject
	private UserRoleResource userRoleResource;

	@Inject
	private UpdateUserTeleLogResources updateUserTeleLogResources;

	@Inject
	private UpdateUserTeleLogApplication updateUserTeleLogApplication;


	/**
	 * 获取当前登录用户信息
	 * @param principal
	 * @return
	 */
//	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_PARTY_ADMIN') or hasRole('ROLE_PARTY_ADMIN_SUPER')")
	@RequestMapping(path = "/user/principal", method = { RequestMethod.GET, RequestMethod.POST })
	@TargetDataSource(name="read")
	public UserInfoDto userInfo(Principal principal){
		UserInfoDto userInfoDto = new UserInfoDto();
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(null != user){
			userInfoDto.setName(user.getName());
			if(!StringUtils.isNull(user.getLogo())){
				userInfoDto.setLogo(userLogoUrl+user.getLogo());
			}else{
				userInfoDto.setLogo(defaultUserLogoUrl);
			}
			userInfoDto.setTelephone(user.getTelePhone());
			userInfoDto.setUserId(user.getId());
			userInfoDto.setLastLoginDate(RelativeDateFormat.format(user.getLastLoginDate()));
			userInfoDto.setHash(user.getHash());
			userInfoDto.setUsername(user.getUsername());
			userInfoDto.setLastLoginTime(user.getLastLoginDate());
			userInfoDto.setAuthorities(user.getAuthorities());
			Set<UserRole> userRoles = user.getUserroles();
			if(null != userRoles && userRoles.size() > 0){
				List<UserRoleDto> roleDtos = new ArrayList<UserRoleDto>();
				UserRoleDto roleDto = null;
				for(UserRole userRole:userRoles){
					if(userRole.getDelFlag().equals("0")){
						roleDto = new UserRoleDto();
						Role role = userRole.getRole();
						if(null != role){
							roleDto.setRoleName(role.getName());
						}
						roleDto.setUserId(userRole.getAuthenticationUserId());
						roleDtos.add(roleDto);
					}
				}
				userInfoDto.setRoles(roleDtos);
			}
		}
		return userInfoDto;
	}

	/**
	 * 验证邮箱 是否注册
	 */
	@RequestMapping(path="/internet/checkEmailRegister/{email}/",method=RequestMethod.GET)
	@TargetDataSource(name="read")
	public boolean checkEmailRegister(@PathVariable("email") String email){
		User user  = userResource.getUserByUserEmail(email);
		if(null!=user){
			return false;
		}
		return true;
	}
	/**
	 * 验证用户�是否注册
	 */
	@RequestMapping(path="/internet/checkUsernameRegister/{username}/",method=RequestMethod.GET)
	@TargetDataSource(name="read")
	public boolean checkUsernameRegister(@PathVariable("username") String username){
		User user  = userResource.getUserByAccount(username);
		if(null!=user){
			return false;
		}
		return true;
	}
	/**
	 * 验证手机�是否注册
	 */
	@RequestMapping(path="/internet/checkTelephoneRegister/{telephone}/",method=RequestMethod.GET)
	@TargetDataSource(name="read")
	public boolean checkTelephoneRegister(@PathVariable("telephone") String telephone){
		User user  = userResource.getUserByPhone(telephone);
		if(null!=user){
			return false;
		}
		return true;
	}
	
	/**
	 * 修改密码
	 * @return
	 */
	@RequestMapping(path="/internet/userPwdModify",method=RequestMethod.POST)
	@TargetDataSource(name="write")
	public Map<String,Object> userPwdModify(@Valid @RequestBody UserPwdModifyParam param){
		Map<String, Object> res = new HashMap<String, Object>();
		String newpwd = param.getNewPassword();
		String oldpwd = param.getOldPassword();
		boolean checkFlag=false;
		checkFlag = StringUtils.validatePassword(newpwd);
		if (!checkFlag) {
			res.put("flag", "fail");
			res.put("message", "密码强度较弱，请混合使用字母和数字，避开常用密码");
			return res;
		}
//		Zxcvbn zxcvbn = new Zxcvbn();
//		Strength strength = zxcvbn.measure(newpwd);
//		if(null != strength){
//			int score = strength.getScore();
//			if(score<2){
//				res.put("flag", "fail");
//				res.put("message", "密码强度较弱，请混合使用字母和数字，避开常用密码!");
//				return res;
//			}
//		}
		User user = userResource.getUserByUserId(param.getUserId());
		if(null == user){
			res.put("flag", "fail");
			res.put("message", "用户不存在");
			return res;
		}
		if(StringUtils.isEmpty(oldpwd) || 
				!encoder.matches(oldpwd, user.getPassword())){
			res.put("flag", "fail");
			res.put("message", "旧密码错误");
			return res;
		}
		if(StringUtils.isEmpty(newpwd)){
			res.put("flag", "fail");
			res.put("message", "新密码不能为空");
			return res;
		}
		user.setPassword(encoder.encode(newpwd));
		userResource.save(user);
		res.put("flag", "success");
		res.put("message", "修改成功");
		return res;
	}
	
	@RequestMapping(value = { "/internet/headpicUpload/{userId}/" }, method = RequestMethod.POST)
	@ResponseBody
	@TargetDataSource(name="write")
	public String Upload(HttpServletRequest request,@PathVariable("userId")String userId) throws ServletException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		String fileName = null;
		InputStream ins = null;
		FileOutputStream fos = null;
		if(StringUtils.isNull(userId)){
			return null;
		}
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile mf = entity.getValue();
			if(null != mf){
				fileName = mf.getOriginalFilename();
			}
			try {
				String suffix = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf("."), fileName.length()) : null;
				fileName = StringUtils.toString(idGenerator.next())+suffix;
				ins = mf.getInputStream();
//				fos = new FileOutputStream(uploadLogoUrl + fileName);
//				byte[] b = new byte[1024];
//				int len = 0;
//				while ((len = ins.read(b)) != -1) {
//					fos.write(b, 0, len);
//				}
				FtpUtil ftputil = new FtpUtil();
				String tempPath = configApp.queryConfigInfoByCode(ConfigEnum.TEMP_EXPORT_PATH.getCode()).getConfigValue();
				FTPClient ftp = ftputil.getClient(configApp.queryConfigInfoByCode(ConfigEnum.SERVER.getCode()).getConfigValue(),
						StringUtils.toInteger(configApp.queryConfigInfoByCode(ConfigEnum.PORT.getCode()).getConfigValue()),
						configApp.queryConfigInfoByCode(ConfigEnum.SERVER_ACCOUNT.getCode()).getConfigValue(),
						configApp.queryConfigInfoByCode(ConfigEnum.SERVER_PWD.getCode()).getConfigValue());
				FtpUtil.upload(ftp, configApp.queryConfigInfoByCode(ConfigEnum.EXPORT_PATH.getCode()).getConfigValue() + fileName, ins);
				FileUtil.deleteDir(new File(tempPath + fileName));
				int flag = userResource.modifyUserLogo(userId,fileName);
				if(flag == 1){
					if(!StringUtils.isNull(fileName)){
						return userLogoUrl+fileName;
					}
				}
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if(null != ins){
						ins.close();
					}
					if(null != fos){
						fos.close();
					}
				} catch (Exception e2) {
					return null;
				}
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/internet/getUserLogo/{username}/", method = RequestMethod.GET)
	public String Upload(@PathVariable("username")String username){
		String logo = userApp.getUserLogoByUsername(username);
		if(!StringUtils.isNull(logo)){
			return "http://"+userLogoUrl+logo;
		}
		return null;
	}
	
	/**
	 * 找回密码
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/retrievePassword",method=RequestMethod.POST)
	@TargetDataSource(name="write")
	public Map<String,Object> retrievePassword(@Valid @RequestBody RetrievePasswordParam param){
		Map<String,Object> map = new HashMap<String,Object>();
		String pwd = param.getNewPassword();
		boolean checkFlag=false;
		checkFlag = StringUtils.validatePassword(pwd);
		if (!checkFlag) {
			map.put("flag", "fail");
			map.put("message", "密码强度较弱，请混合使用字母和数字，避开常用密码");
			return map;
		}
//		Zxcvbn zxcvbn = new Zxcvbn();
//		Strength strength = zxcvbn.measure(pwd);
//		if(null != strength){
//			int score = strength.getScore();
//			if(score<2){
//				map.put("flag", "fail");
//				map.put("message", "密码强度较弱，请混合使用字母和数字，避开常用密码!");
//				return map;
//			}
//		}
		User user = userApp.getUserByUserName(param.getUsername());
		if(null == user){
			map.put("flag", "fail");
			map.put("message", "该用户不存在");
			return map;
		}
		if(null == user.getTelePhone()){
			map.put("flag", "fail");
			map.put("message", "该用户没有绑定手机");
			return map;
		}
		//校验验证�
		map = smClient.isVerifyCodeValid(user.getTelePhone(),SendMessageConstants.RETRIEVEPASSWORD,param.getNewVericode());
//		map = commonApplication.isVerifyCodeValid(user.getTelePhone(),SendMessageConstants.RETRIEVEPASSWORD,param.getNewVericode());
		if(null != map && map.get("flag").equals("fail")){
			return map;
		}
		if(StringUtils.isNull(param.getNewVericode())){
			map.put("flag", "fail");
			map.put("message", "验证码错误");
			return map;
		}
		user.setPassword(encoder.encode(pwd));
		userResource.save(user);
		map.put("flag", "success");
		map.put("message", "修改成功");
		return map;
	}
	
	/**
	 * 验证原手机号
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/internet/oldphoneVerify",method=RequestMethod.POST)
	@TargetDataSource(name="read")
	public Map<String,Object> oldphoneVerify(@Valid @RequestBody OldphoneVerifyParam param){
		Map<String,Object> map = new HashMap<String,Object>();
		String oldPhone = param.getOldPhone();
		String userId = param.getUserId();
		String vericode = param.getVerifyCode();
		//增加短信验证
		map = smClient.isVerifyCodeValid(oldPhone,SendMessageConstants.OLDPHONEVERIFY,vericode);
//		map = commonApplication.isVerifyCodeValid(oldPhone,SendMessageConstants.OLDPHONEVERIFY,vericode);
		if(null != map && map.get("flag").equals("fail")){
			return map;
		}
		User u = userResource.getUserByUserId(userId);
		if(null == u){
			map.put("flag", "fail");
			map.put("message", "该用户不存在");
			return map;
		}
		if(!oldPhone.equals(u.getTelePhone())){
			map.put("flag", "fail");
			map.put("message", "原手机号错误");
			return map;
		}
		map.put("flag", "success");
		map.put("message", "验证成功");
		return map;
	}
	
	/**
	 * 验证新手机号并绑�
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/internet/bindNewphone",method=RequestMethod.POST)
	@TargetDataSource(name="write")
	public Map<String,Object> bindNewphone(@Valid @RequestBody BindNewphoneParam param){
		Map<String,Object> map = new HashMap<String,Object>();
		String newPhone = param.getNewPhone();
		String userId = param.getUserId();
		String vericode = param.getVerifyCode();
		//增加短信验证
		map = smClient.isVerifyCodeValid(newPhone,SendMessageConstants.NEWPHONEVERIFY,vericode);
//		map = commonApplication.isVerifyCodeValid(newPhone,SendMessageConstants.NEWPHONEVERIFY,vericode);
		if(null != map && map.get("flag").equals("fail")){
			return map;
		}
		User u = userResource.getUserByUserId(userId);
		if(null == u){
			map.put("flag", "fail");
			map.put("message", "该用户不存在");
			return map;
		}
		if(u.getTelePhone().equals(newPhone)){
			map.put("flag", "fail");
			map.put("message", "新手机号与原手机号相同，无需重新绑定");
			return map;
		}
		User otherUser = userResource.getUserByPhone(newPhone);
		if(null != otherUser){
			map.put("flag", "fail");
			map.put("message", "该手机号已经被绑");
			return map;
		}
		u.setTelePhone(newPhone);
		u.setUsername(newPhone);
		u.setUpdateDate(new Date());
		userResource.save(u);
		map.put("flag", "success");
		map.put("message", "绑定成功");
		return map;
	}
	
	/**
	 * 根据用户名获取绑定手机号
	 * @return
	 */
	@RequestMapping(path="/gettelephoneByusername",method=RequestMethod.POST)
	@TargetDataSource(name="read")
	public String gettelephoneByusername(@RequestBody GetPhoneParam username){
		String telephone = null;
		User user = userResource.getUserByAccount(username.getUsername());
		if(null != user){
			telephone = user.getTelePhone();
		}
		return telephone;
	}
	
	
	//--------------互联网web端独有找回密�-------------------
	/**
	 * 验证找回密码短信验证码是否正�
	 */
	@RequestMapping(path="/verifyRetrievePwdVerificationCode",method=RequestMethod.POST)
	@ResponseBody
	@TargetDataSource(name="read")
	public Map<String, Object> verifyRetrievePwdVerificationCode(@Valid @RequestBody VerifyRtPwdVfCodeParam param){
		User u = userResource.getUserByPhone(param.getTelePhone());
		Map<String, Object> map = new HashMap<String,Object>();
		if(null == u){
			map.put("flag", "fail");
			map.put("message", "不存在该用户!");
			return map;
		}
		map = smClient.isVerifyCodeValid(param.getTelePhone(),SendMessageConstants.RETRIEVEPASSWORD,param.getVerificationCode());
//		map = commonApplication.isVerifyCodeValid(param.getTelePhone(),SendMessageConstants.RETRIEVEPASSWORD,param.getVerificationCode());
		if(map.get("flag") != null && map.get("flag").toString().equals("success")){
			map.put("message", u.getId());
		}
		return map;
	}
	
	/**
	 * 找回密码
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/retrievePasswordForWeb",method=RequestMethod.POST)
	@ResponseBody
	@TargetDataSource(name="write")
	public Map<String,Object> retrievePasswordForWeb(@Valid @RequestBody RetrievePasswordForWebParam param){
		Map<String,Object> map = new HashMap<String,Object>();
		String pwd = param.getPassword();
		boolean checkFlag=false;
		checkFlag = StringUtils.validatePassword(pwd);
		if (!checkFlag) {
			map.put("flag", "fail");
			map.put("message", "密码强度较弱，请混合使用字母和数字，避开常用密码");
			return map;
		}
//		Zxcvbn zxcvbn = new Zxcvbn();
//		Strength strength = zxcvbn.measure(pwd);
//		if(null != strength){
//			int score = strength.getScore();
//			if(score<2){
//				map.put("flag", "fail");
//				map.put("message", "密码强度较弱，请混合使用字母和数字，避开常用密码!");
//				return map;
//			}
//		}
		User user = userResource.getUserByUserId(param.getUserId());
		if(null == user){
			map.put("flag", "fail");
			map.put("message", "该用户不存在");
			return map;
		}
		user.setPassword(encoder.encode(pwd));
		userResource.save(user);
		map.put("flag", "success");
		map.put("message", "修改成功");
		return map;
	}
	
	/**
	 * 验证手机号是否注�
	 * @return
	 */
	@RequestMapping(path="/common/checkPhoneRegist/{telephone}", method={RequestMethod.GET})
	@TargetDataSource(name="read")
	public boolean checkPhoneRegist(@PathVariable("telephone") String telephone){
		//如果该短信是找回密码，则需要判断这个手机号是否已经注册过了
		User u = userResource.getUserByPhone(telephone);
		if(u == null){
			return false;
		}
		return true;
	}
	
	//--------------互联网web端独有找回密�-------------------

	/**
	 * �修改头像
	 * @return
	 * @throws ServletException
     */
	@RequestMapping(value = { "/internet/updateUserLogo" }, method = RequestMethod.POST)
	@ResponseBody
	@TargetDataSource(name="write")
	public boolean updateUserLogo(@Valid @RequestBody UpdateUserLogoParam param) throws ServletException {
		String fileName = param.getFileName();
		String userId = param.getUserId();
		if(StringUtils.isNull(userId)){
			return false;
		}
		int flag = userResource.modifyUserLogo(userId,fileName);
		if(flag == 1){
			if(!StringUtils.isNull(fileName)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 用户添加认证
	 * @return
	 * @throws ServletException
	 */
	@RequestMapping(value = { "/internet/addAuthentication" }, method = RequestMethod.POST)
	@ResponseBody
	@TargetDataSource(name="write")
	public Map<String,String> addAuthentication(@Valid @RequestBody AddAuthenticationParam param) throws ServletException {
		Map<String,String> map = new HashMap<>();
		String idCard = param.getIdCard();
		String uniquekey = param.getUniqueKey();
		String relName = param.getRelName();
		String userId = param.getUserId();
		String hash = null;
		String userHash = StringUtils.getIDHash(idCard);
		String authIdcard = null;
		String authRelname = null;
		String authUniqueKey = null;
		String authDelflag = null;
		UserRole userRole = null;
		List<UserRole> userRoleList  = null;
		//获取idCard uniquekey relName 的认证消�
		List<AuthenticationInfo> iList =
				authenticationInfoResources.getAuthByIdcardkeyrelname(idCard,uniquekey,relName);
		//根据userId获取用户的hash
		User u = userResource.getUserById(userId);
		if(null != u){
			hash = u.getIdCardHash();
		}
		if(!userHash.equals(hash)){
			map.put("flag","fail");
			map.put("message","当前用户和所填写身份证不属于同一人！");
			return map;
		}
		//判断该认证是否属于该用户
		if(ListUntils.isNotNull(iList)){
			userRoleList = new ArrayList<>();
			for(AuthenticationInfo ai:iList){
				authIdcard = ai.getIdCard();
				authUniqueKey = ai.getUniqueKey();
				authRelname = ai.getRelName();
//				authDelflag = ai.getDelFlag();
				if(!authIdcard.equals(idCard)){
					map.put("flag","fail");
					map.put("message","该认证信息不属于该用户！");
					return map;
				}
//				//检测该认证是否已经绑定
//				if("1".equals(authDelflag)){
//					map.put("flag","fail");
//					map.put("message","该认证信息已经绑定！");
//					return map;
//				}
				//如果正确的话，则绑定该角
				userRole = new UserRole();
				userRole.setRelId(idGenerator.next());
				userRole.setRoleId(StringUtils.toLong(ai.getRoleId()));
				userRole.setUserId(userId);
				userRole.setAuthenticationUserId(ai.getUserId());
				userRole.setCreateDate(new Date());
				userRole.setDelFlag("0");
				userRoleList.add(userRole);

				ai.setDelFlag("1");
			}
			userRoleResource.save(userRoleList);
			authenticationInfoResources.save(iList);
		}else{
			map.put("flag","fail");
			map.put("message","没有其他认证信息!");
			return map;
		}
		map.put("flag","success");
		map.put("message","认证成功!");
		return map;
	}

	/**
	 * 管理员给忘记密码人员更换手机号
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/internet/resetTelePhoneByAdmin",method=RequestMethod.POST)
	@ResponseBody
	@TargetDataSource(name="write")
	public Map<String,Object> resetTelePhoneByAdmin(@Valid @RequestBody ResetTelePhoneDto param)  throws  Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapCode = new HashMap<String, Object>();
		String oldTele = null;
		String newTele =null;
		String idCard = null;
		String name =null;
		boolean flag =false;
		if (null != param) {
			oldTele = param.getOldTelephone().trim();
			newTele = param.getNewTelephone().trim();
			idCard = param.getIdCard().trim();
			name = param.getRelName().trim();
			String hash =StringUtils.getUserNameIDHash(name,idCard);
			User u = userResource.getUserByIdcardHash(hash);
			if(null!=u){
				if(oldTele.equals(newTele)){
					map.put("flag","fail");
					map.put("message","两次输入的手机号相同");
					return map;
				}
				if(!u.getTelePhone().equals(oldTele)){
					map.put("flag","fail");
					map.put("message","该党员旧手机号不正确");
					return map;
				}
				if(u.getUsername().equals(newTele)){
					map.put("flag","fail");
					map.put("message","新手机号与旧手机号不能相同");
					return map;
				}
				if(!u.getIdCardHash().equals(StringUtils.getIDHash(idCard))){
					map.put("flag","fail");
					map.put("message","该党员身份证号与输入不匹配");
					return map;
				}
				if(!u.getHash().equals(hash)){
					map.put("flag","fail");
					map.put("message","该党员真实姓名身份证号与输入不匹配");
					return map;
				}

			    User uu= userResource.getUserByPhone(newTele);
				 if(null!=uu){
					map.put("flag","fail");
					map.put("message","新手机号已存在");
					return map;
				}

			// TODO 第一书记
				flag=this.CheckUserExistFromSimple(hash);
				if(!flag){
					map.put("flag","fail");
					map.put("message","输入的人员不是党员身份");
					return map;
				}

				//发送短信并进行更新操作同时记录日志
				mapCode = smClient.isVerifyCodeValid(newTele, SendMessageConstants.NEWPHONEVERIFY, param.getVerifyCode());
				if(mapCode.containsKey("flag") && mapCode.get("flag").equals("fail")){
					map.put("flag","fail");
					map.put("message","验证码不正确");
					return map;
				}
  				updateUserTeleLogApplication.addUpdateUserTeleLog(oldTele, newTele, name, idCard, hash,
							param.getHash(), param.getUpdateBy(), param.getUpdateName(), param.getVerifyCode());
				/**
				 * 同步简向库更新手机号
				 */
				this.updateSycnSimple(name,idCard);

				map.put("flag","success");
				map.put("message","修改成功");
				return map;
			}else{
				map.put("flag","fail");
				map.put("message","该党员旧手机号不正确");
				return map;
			}
		}
		return map;
	}


	public  boolean   updateSycnSimple(String name,String idCard){

		RestTemplate restTemplate = new RestTemplate();
		boolean flag=false;
		List<Object[]> newList = new ArrayList<>();
		String hash =StringUtils.getUserNameIDHash(name,idCard);

		newList=userApp.updateTeleByAdminToSimple(hash);
		if(ListUntils.isNotNull(newList)){
			HttpEntity entity = new HttpEntity(newList);
			flag=restTemplate.exchange(sendHashurl, HttpMethod.POST, entity, boolean.class).getBody();
		}
		if (!flag) {
			log.error("===============更新后数据通信错误，请联系管理员！");
			return false;
		}
		return flag;

	}

	/**
	 * 判断是否是党员或者是第一书记角色
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/checkIsParty",method=RequestMethod.POST)
	@ResponseBody
	@TargetDataSource(name="write")
	public boolean checkIsParty(@Valid @RequestBody InCheckIsPartyDto param)  throws  Exception{
		String hash=null;
		String name=null;
		String idCard=null;
		boolean flag=false;
		if(null!=param){
		  name=param.getRelName();
		  idCard=param.getIdCard();
		  hash=StringUtils.getUserNameIDHash(name,idCard);
		 flag= this.CheckUserExistFromSimple(hash);
			if(flag){
				return true;
			}else{
				return false;
			}
		}
		return false;

	}




	/**
	 * 调用简向库查询是否实名认证备案的党员
	 *
	 * @param hash
	 * @return
	 */
	public boolean CheckUserExistFromSimple(String hash) {

		RestTemplate restTemplate = new RestTemplate();

		boolean receiveValue = false;
		HttpEntity<String> entity = new HttpEntity(hash);

		try {
			receiveValue = restTemplate.exchange(checkUserUrl, HttpMethod.POST, entity, boolean.class).getBody();
		} catch (Exception e) {
			e.printStackTrace();
			return receiveValue;
		}
		return receiveValue;
	}


}
