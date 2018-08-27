/**
 * 
 */
package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.base.ConfigEnum;
import com.ido85.party.aaaa.mgmt.dto.userinfo.ModifyMobileParam;
import com.ido85.party.aaaa.mgmt.dto.userinfo.ModifyPasswordParam;
import com.ido85.party.aaaa.mgmt.dto.userinfo.ModifyVpnPasswordParam;
import com.ido85.party.aaaa.mgmt.dto.userinfo.UserPwdModifyParam;
import com.ido85.party.aaaa.mgmt.ldap.application.LdapApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.ConfigApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.UserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserVpnRel;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserResources;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserVpnRelResources;
import com.ido85.party.aaaa.mgmt.security.common.UserUtils;
import com.ido85.party.aaaa.mgmt.security.utils.FileUtil;
import com.ido85.party.aaaa.mgmt.security.utils.FtpUtil;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author rongxj
 *
 */
@RestController
public class UserController {
	@Inject
	private UserApplication userApp;
	@Value("${userLogoUrl}")
	private String userLogoUrl;
	@Inject
	private IdGenerator idGenerator;
	@Value("${uploadLogoUrl}")
	private String uploadLogoUrl;
	@Inject
	private UserResources userResources;
	@Inject
	private ConfigApplication configApp;
	@Inject
	private UserVpnRelResources userVpnRelResources;
	@Inject
	private LdapApplication ldapApplication;
	@Inject
	private PasswordEncoder encoder;

	/**
	 * 验证邮箱是否注册
	 */
	@RequestMapping(path="/user/checkEmailRegister/{email}/",method=RequestMethod.GET)
	public boolean checkEmailRegister(@PathVariable("email") String email){
		User user  = userApp.loadUserByUserEmail(email);
		if(null!=user){
			return false;
		}
		return true;
	}
	/**
	 * 获取头像
	 */
	@RequestMapping(path="/manage/user/getUserLogo/{userName}",method=RequestMethod.GET)
	public String getUserLogo(@PathVariable("userName") String userName){
		User user  = userApp.getUserByUserName(userName);
		if(null!=user){
			String logo = user.getLogo();
			return userLogoUrl+logo;
		}
		return null;
	}
	
	@RequestMapping(value = { "/manage/headpicUpload/{username}/" }, method = RequestMethod.POST)
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
				FtpUtil ftputil = new FtpUtil();
				String tempPath = configApp.queryConfigInfoByCode(ConfigEnum.TEMP_EXPORT_PATH.getCode()).getConfigValue();
				FTPClient ftp = ftputil.getClient(configApp.queryConfigInfoByCode(ConfigEnum.SERVER.getCode()).getConfigValue(),
//						StringUtils.toInteger(configApp.queryConfigInfoByCode(ConfigEnum.PORT.getCode()).getConfigValue()),
						configApp.queryConfigInfoByCode(ConfigEnum.SERVER_ACCOUNT.getCode()).getConfigValue(),
						configApp.queryConfigInfoByCode(ConfigEnum.SERVER_PWD.getCode()).getConfigValue());
				FtpUtil.upload(ftp, configApp.queryConfigInfoByCode(ConfigEnum.USERLOGO_EXPORT_PATH.getCode()).getConfigValue() + fileName, ins);
				FileUtil.deleteDir(new File(tempPath + fileName));
				int flag = userResources.modifyUserLogo(username,fileName);
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

	/**
	 *修改密码
	 */
	@RequestMapping(path="/forget/modifyPassword",method=RequestMethod.POST)
	public Map<String,Object> modifyPassword(@Valid @RequestBody ModifyPasswordParam param){
		return userApp.modifyPassword(param);
	}

	/**
	 * 修改vpn密码
	 */
	@RequestMapping(path="/manage/user/modifyVpnPassword",method=RequestMethod.POST)
	public Map<String,Object> modifyVpnPassword(@Valid @RequestBody ModifyVpnPasswordParam param){
		Map<String,Object> map = new HashMap<>();
		String pwd = param.getPassword();
		String vpn = param.getVpn();
		String userVpn = null;

		User u = UserUtils.getCurrentUser();
		if(null == u){
			map.put("flag","fail");
			map.put("message","用户不存在");
			return map;
		}
		UserVpnRel rel = userVpnRelResources.getRelByUserId(u.getId());
		if(null != rel){
			userVpn = rel.getVpn();
			if(userVpn.equals(vpn)){
				//进行密码修改
				boolean flag = ldapApplication.updateVpnPwd(userVpn,rel.getOuName(),pwd);
				if(flag){
					map.put("flag","success");
					map.put("message","修改成功");
					return map;
				}
			}
			else {
				map.put("flag", "fail");
				map.put("message", "VPN账号错误");
				return map;
			}
		}
		map.put("flag","fail");
		map.put("message","修改失败");
		return map;
	}

	/**
	 * 检测手机号是否被注册
	 * false:已注册   true:未注册
	 */
	@RequestMapping(path="/mgmt/checkTelephoneRegister/{telephone}",method=RequestMethod.GET)
	public boolean checkTelephoneRegister(@PathVariable("telephone")String telephone){
		User user = userResources.getUserByTelephone(telephone);
		return null == user;
	}

	/**
	 * 修改密码
	 * @return
	 */
	@RequestMapping(path="/manage/userPwdModify",method=RequestMethod.POST)
	public Map<String,Object> userPwdModify(@Valid @RequestBody UserPwdModifyParam param){
		Map<String, Object> res = new HashMap<String, Object>();
		User user = UserUtils.getCurrentUser();
		if(null == user){
			res.put("result", "fail");
			res.put("info", "用户不存在");
			return res;
		}
		User u = userResources.getUserByUserId(user.getId());
		if(StringUtils.isEmpty(param.getOldPassword()) ||
				!encoder.matches(param.getOldPassword(), u.getPassword())){
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
		userResources.save(user);
		res.put("result", "success");
		res.put("info", "修改成功");
		return res;
	}


	/**
	 * 更换手机号
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/user/modifyMobile",method=RequestMethod.POST )
	public  Map<String,Object> modifyMobile(@Valid @RequestBody ModifyMobileParam param){
		Map<String,Object>  map = new HashMap<>();

		String oldMobile = param.getOldMobile();//旧手机号
		String newMobile = param.getNewMobile();//新手机号
		String verifyCode = param.getVerifyCode();//短信验证码
		boolean checkFlag=false;
		Map<String, Object> mobileMap = new HashMap<>();

		//获取当前用户
		User user = UserUtils.getCurrentUser();
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
			mobileMap =userApp.checkVerifyCode(newMobile,verifyCode);
			if(mobileMap.containsKey("flag") && mobileMap.get("flag").equals("fail")) {
				map.put("flag", "fail");
				map.put("message", mobileMap.get("message"));
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
		u =userResources.checkMobile(oldMobile);
		return null!=u?true:false;
	}

	//判断新手机是否存在
	private boolean checkNewMobileIsExists(String newMobile){
		User u;
		u =userResources.checkNewMobile(newMobile);
		return null!=u?true:false;
	}

}
