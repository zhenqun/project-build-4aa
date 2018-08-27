/**
 * 
 */
package com.ido85.party.sso.controller;

import com.ido85.party.sso.distribute.generator.IdGenerator;
import com.ido85.party.sso.dto.*;
import com.ido85.party.sso.security.authentication.application.CommonApplication;
import com.ido85.party.sso.security.authentication.application.ConfigApplication;
import com.ido85.party.sso.security.authentication.application.UserApplication;
import com.ido85.party.sso.security.authentication.domain.Role;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.domain.UserRole;
import com.ido85.party.sso.security.authentication.domain.UserVpnRel;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.authentication.repository.UserVpnResources;
import com.ido85.party.sso.security.constants.ConfigEnum;
import com.ido85.party.sso.security.constants.SendMessageConstants;
import com.ido85.party.sso.security.ldap.application.LdapApplication;
import com.ido85.party.sso.security.utils.FileUtil;
import com.ido85.party.sso.security.utils.FtpUtil;
import com.ido85.party.sso.security.utils.RelativeDateFormat;
import com.ido85.party.sso.security.utils.StringUtils;
import com.ido85.party.sso.service.VpnClient;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
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
public class UserController {

	@Inject
	private UserDetailsManager userDetailsManager;
	
	@Inject
	private UserResources userResource;
	
	@Inject
	private PasswordEncoder encoder;
	
	@Inject
	private UserApplication userApp;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Value("${userLogoUrl}")
	private String userLogoUrl;
	
	@Value("${uploadLogoUrl}")
	private String uploadLogoUrl;
	
	@Value("${queryUserInfoByHashUrl}")
	private String queryUserInfoByHashUrl;
	
	@Inject
	private CommonApplication commonApplication;
	
	@Value("${defaultUserLogoUrl}")
	private String defaultUserLogoUrl;
	
	@Inject
	private VpnClient vpnClient;
	
	@Inject
	private UserVpnResources userVpnResources;
	
	@Inject
	private ConfigApplication configApp;

	@Inject
	private LdapApplication ldapApplication;
	/**
	 * 获取当前登录用户信息
	 * @param principal
	 * @return
	 */
//	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_PARTY_ADMIN') or hasRole('ROLE_PARTY_ADMIN_SUPER')")
	@RequestMapping(path = "/user/principal", method = { RequestMethod.GET, RequestMethod.POST })
	public UserInfoDto userInfo(Principal principal,HttpServletRequest request){
		UserInfoDto userInfoDto = new UserInfoDto();
		RoleDto roleDto = null;
		Role role = null;
		User user = null;
		user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(null != user){
			userInfoDto.setName(user.getName());
			userInfoDto.setHash(user.getHash());
			userInfoDto.setLastLoginDate(RelativeDateFormat.format(user.getLastLoginDate()));
			if(!StringUtils.isNull(user.getLogo())){
				userInfoDto.setLogo(userLogoUrl+user.getLogo());
			}else{
				userInfoDto.setLogo(defaultUserLogoUrl);
			}
			userInfoDto.setLastLoginTime(user.getLastLoginDate());
			userInfoDto.setUserId(user.getId());
			Set<UserRole> roles = user.getRoles();
			List<RoleDto> roleDtos = new ArrayList<>();
			if(null != roleDtos && roles.size() > 0){
				for(UserRole ur:roles){
					roleDto = new RoleDto();
					roleDto.setManageCode(ur.getManageCode());
					roleDto.setManageId(ur.getManageId());
					roleDto.setManageName(ur.getManageName());
					role = ur.getRole();
					if(null != role){
						roleDto.setClientId(role.getClientId());
						roleDto.setRoleId(StringUtils.toString(role.getId()));
						roleDto.setRoleDescription(role.getDescription());
						roleDto.setRoleName(role.getName());
						roleDto.setPermissions(role.getPermissions());
					}
					roleDtos.add(roleDto);
				}
			}
			userInfoDto.setRoles(roleDtos);
		}
		return userInfoDto;
	}

	
//	/**
//	 * 验证邮箱 是否注册
//	 */
//	@RequestMapping(path="/acc/checkEmailRegister/{email}/",method=RequestMethod.GET)
//	public boolean checkEmailRegister(@PathVariable("email") String email){
//		User user  = userResource.getUserByUserEmail(email);
//		if(null!=user){
//			return false;
//		}
//		return true;
//	}
	/**
	 * 验证用户名 是否注册
	 */
	@RequestMapping(path="/acc/checkUsernameRegister/{username}/",method=RequestMethod.GET)
	public boolean checkUsernameRegister(@PathVariable("username") String username){
		User user  = userResource.getUserByAccount(username);
		if(null!=user){
			return false;
		}
		return true;
	}
	/**
	 * 验证手机号 是否注册
	 */
	@RequestMapping(path="/acc/checkTelephoneRegister/{telephone}/",method=RequestMethod.GET)
	public boolean checkTelephoneRegister(@PathVariable("telephone") String telephone){
		User user  = userResource.getUserByPhone(telephone);
		if(null!=user){
			return false;
		}
		return true;
	}
	//------------新修改密码-----------------
	/**
	 * 修改密码
	 * @return
	 */
	@RequestMapping(path="/acc/userPwdModify",method=RequestMethod.POST)
	public Map<String,Object> userPwdModify(@Valid @RequestBody UserPwdModifyParam param){
		Map<String, Object> res = new HashMap<String, Object>();
		boolean checkFlag=false;
		checkFlag = StringUtils.validatePassword(param.getNewPassword());
		if (!checkFlag) {
			res.put("flag", "fail");
			res.put("message", "密码强度较弱，请混合使用大小写字母和数字，避开常用密码");
			return res;
		}
//		Zxcvbn zxcvbn = new Zxcvbn();
//		Strength strength = zxcvbn.measure(param.getNewPassword());
//		if(null != strength){
//			int score = strength.getScore();
//			if(score<2){
//				res.put("flag", "fail");
//				res.put("message", "密码强度较弱，请混合使用字母和数字，避开常用密码!");
//				return res;
//			}
//		}
		User user = (User)userDetailsManager.loadUserByUsername(param.getUsername());
		if(null == user){
			res.put("result", "fail");
			res.put("info", "用户不存在");
			return res;
		}
		if(StringUtils.isEmpty(param.getOldPassword()) ||
				!encoder.matches(param.getOldPassword(), user.getPassword())){
			res.put("result", "fail");
			res.put("info", "旧密码错误");
			return res;
		}
		if(StringUtils.isEmpty(param.getNewPassword())){
			res.put("result", "fail");
			res.put("info", "新密码不能为空");
			return res;
		}
		user.setPassword(encoder.encode(param.getNewPassword()));
		userResource.save(user);
		res.put("result", "success");
		res.put("info", "修改成功");
		return res;
	}

	@RequestMapping(value = { "/acc/headpicUpload/{username}/" }, method = RequestMethod.POST)
	@ResponseBody
	public String Upload(HttpServletRequest request,@PathVariable("username")String username) throws ServletException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		String fileName = null;
		InputStream ins = null;
		FileOutputStream fos = null;
		if(StringUtils.isNull(username)){
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

				//
				FtpUtil ftputil = new FtpUtil();
				String tempPath = configApp.queryConfigInfoByCode(ConfigEnum.TEMP_EXPORT_PATH.getCode()).getConfigValue();
				FTPClient ftp = ftputil.getClient(configApp.queryConfigInfoByCode(ConfigEnum.SERVER.getCode()).getConfigValue(),
						StringUtils.toInteger(configApp.queryConfigInfoByCode(ConfigEnum.PORT.getCode()).getConfigValue()),
						configApp.queryConfigInfoByCode(ConfigEnum.SERVER_ACCOUNT.getCode()).getConfigValue(),
						configApp.queryConfigInfoByCode(ConfigEnum.SERVER_PWD.getCode()).getConfigValue());
				FtpUtil.upload(ftp, configApp.queryConfigInfoByCode(ConfigEnum.EXPORT_PATH.getCode()).getConfigValue() + fileName, ins);
				FileUtil.deleteDir(new File(tempPath + fileName));

				int flag = userResource.modifyUserLogo(username,fileName);
				if(flag == 1){
					if(!StringUtils.isNull(fileName)){
						return "http://"+userLogoUrl+fileName;
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

	@RequestMapping(value = "/acc/getUserLogo/{username}/", method = RequestMethod.GET)
	public String Upload(@PathVariable("username")String username){
		String logo = userApp.getUserLogoByUsername(username);
		if(!StringUtils.isNull(logo)){
			return userLogoUrl+logo;
		}
		return null;
	}

	//--------------互联网web端独有找回密码--------------------
	/**
	 * 验证找回密码短信验证码是否正确
	 */
	@RequestMapping(path="/verifyRetrievePwdVerificationCode",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> verifyRetrievePwdVerificationCode(@Valid @RequestBody VerifyRtPwdVfCodeParam param){
		User u = userResource.getUserByPhone(param.getTelePhone());
		Map<String, Object> map = new HashMap<String,Object>();
		if(null == u){
			map.put("flag", "fail");
			map.put("message", "不存在该用户!");
			return map;
		}
		map = commonApplication.isVerifyCodeValid(param.getTelePhone(),SendMessageConstants.RETRIEVEPASSWORD,param.getVerificationCode());
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
	public Map<String,Object> retrievePasswordForWeb(@Valid @RequestBody RetrievePasswordForWebParam param){
		Map<String,Object> map = new HashMap<String,Object>();
		String pwd = param.getPassword();
		boolean checkFlag=false;
		checkFlag = StringUtils.validatePassword(pwd);
		if (!checkFlag) {
			map.put("flag", "fail");
			map.put("message", "密码强度较弱，请混合使用大小写字母和数字，避开常用密码");
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
			map.put("message", "该用户不存在！");
			return map;
		}
		user.setPassword(encoder.encode(pwd));
		userResource.save(user);
		map.put("flag", "success");
		map.put("message", "修改成功");
		return map;
	}

	/**
	 * 验证手机号是否注册
	 * @return
	 */
	@RequestMapping(path="/common/checkPhoneRegist/{telephone}", method={RequestMethod.GET})
	public boolean checkPhoneRegist(@PathVariable("telephone") String telephone){
		//如果该短信是找回密码，则需要判断这个手机号是否已经注册过了
		User u = userResource.getUserByPhone(telephone);
		if(u == null){
			return false;
		}
		return true;
	}
	//--------------互联网web端独有找回密码--------------------


	/**
	 * 修改VPN密码
	 * @return
	 */
	@RequestMapping(path="/acc/userVpnPwdModify",method=RequestMethod.POST)
	public Map<String,String> userVpnPwdModify(@Valid @RequestBody UserVpnPwdModifyParam param,HttpServletRequest request){
		Map<String,String> res = new HashMap<>();
		User user = null;
		SecurityContextImpl securityContextImpl = (SecurityContextImpl)request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
		if(null != securityContextImpl){
			user = (User)securityContextImpl.getAuthentication().getPrincipal();
		}
		if(null == user){
			res.put("flag", "fail");
			res.put("message", "用户不存在");
			return res;
		}
		String vpn = param.getVpn();
		//查询vpn账号以及，ou
		UserVpnRel rel = userVpnResources.getRelByUserId(user.getId(), vpn);
		if(null == rel){
			res.put("flag", "fail");
			res.put("message", "vpn账号错误");
			return res;
		}
		vpn = rel.getVpn();
		String ou = rel.getOu();
		boolean flag = ldapApplication.updateVpnPwd(user.getId(),param.getPassword());
		if(flag){
			res.put("flag", "success");
			res.put("message", "修改成功");
			return res;
		}
		res.put("flag", "fail");
		res.put("message", "修改失败");
		return res;
	}

	/**
	 * 更换手机号
	 * @param param
	 * @return
	 */
	@RequestMapping(path ="/acc/modifyMobile",method=RequestMethod.POST)
	public  Map<String,Object> modifyMobile(@Valid @RequestBody UserModifyMobileParam param,HttpServletRequest request){
		Map<String,Object>  map = new HashMap<>();
		User user = null;
		String oldMobile = param.getOldMobile();//旧手机号
		String newMobile = param.getNewMobile();//新手机号
		String verifyCode = param.getVerifyCode();//短信验证码
		boolean checkFlag=false;

		//获取当前用户
		SecurityContextImpl securityContextImpl = (SecurityContextImpl)request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
		if(null != securityContextImpl){
			user = (User)securityContextImpl.getAuthentication().getPrincipal();
		}
		if(null == user){
			map.put("flag", "fail");
			map.put("message", "用户不存在");
			return map;
		}

		if(oldMobile.equals(newMobile)){
			map.put("flag", "fail");
			map.put("message", "原手机号与新手机号相同");
			return map;
		}
		if(!StringUtils.isEmpty(oldMobile)){
			checkFlag=this.checkOldMobileIsExists(oldMobile);
			if(!checkFlag) {
				map.put("flag", "fail");
				map.put("message", "原手机号不存在");
				return map;
			}
		}
		if(!StringUtils.isEmpty(newMobile)){
			checkFlag = this.checkNewMobileIsExists(newMobile);
			if(checkFlag){
				map.put("flag","fail");
				map.put("message","新手机号已存在");
				return map;
			}
		}
		//验证码
		if(!StringUtils.isEmpty(verifyCode)){
			map =userApp.checkVerifyCode(newMobile,verifyCode);
			if(null != map && map.containsKey("flag") && map.get("flag").equals("fail")){
				return map;
			}
		}
		int updateFlag=userApp.updateMobile(user.getId(),newMobile);
		if(updateFlag<=0){
			map.put("flag","fail");
			map.put("message","修改失败");
			return map;
		}else{
			map.put("message", "修改成功");
			map.put("flag", "success");
			return map;
		}

	}

	//判断旧手机号是否存在
	private  boolean checkOldMobileIsExists(String oldMobile){
		User u;
		u =userResource.checkMobile(oldMobile);
		return null!=u?true:false;
	}

	//判断新手机是否存在
	private boolean checkNewMobileIsExists(String newMobile){
		User u;
		u =userResource.checkNewMobile(newMobile);
		return null!=u?true:false;
	}
}
