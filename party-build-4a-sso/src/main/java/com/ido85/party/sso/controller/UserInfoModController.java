package com.ido85.party.sso.controller;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ido85.party.sso.dto.BindNewphoneParam;
import com.ido85.party.sso.dto.OldphoneVerifyParam;
import com.ido85.party.sso.dto.RetrievePasswordForWebParam;
import com.ido85.party.sso.dto.RetrievePasswordParam;
import com.ido85.party.sso.dto.UserPwdModifyParam;
import com.ido85.party.sso.dto.VerifyRtPwdVfCodeParam;
import com.ido85.party.sso.security.authentication.application.UserApplication;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.constants.SendMessageConstants;
import com.ido85.party.sso.security.utils.StringUtils;
import com.ido85.party.sso.service.SmClient;
import com.ido85.party.sso.service.UserClient;

@RestController
public class UserInfoModController {

	@Inject
	private UserClient userPwdClient;
	
	@Value("${userLogoUrl}")
	private String userLogoUrl;
	
	@Value("${defaultUserLogoUrl}")
	private String defaultUserLogoUrl;
	
	@Value("${uploadLogoUrl}")
	private String uploadLogoUrl;
	
	@Inject
	private UserResources userResource;
	
	@Inject
	private SmClient smClient;
	
	@Inject
	private UserApplication userApp;
	
//	//--------------互联网web端独有找回密码--------------------
//	/**
//	 * 验证找回密码短信验证码是否正确
//	 */
//	@RequestMapping(path="/verifyRetrievePwdVerificationCode",method=RequestMethod.POST)
//	@ResponseBody
//	public Map<String, Object> verifyRetrievePwdVerificationCode(@Valid @RequestBody VerifyRtPwdVfCodeParam param){
//		Map<String, Object> map = new HashMap<String,Object>();
//		User u = userResource.getUserByPhone(param.getTelePhone());
//		if(null == u){
//			map.put("flag", "fail");
//			map.put("message", "不存在该用户!");
//			return map;
//		}
//		map = smClient.verifyRetrievePwdVerificationCode(param);
//		if(map.get("flag") != null && map.get("flag").toString().equals("success")){
//			map.put("message", u.getId());
//		}
//		return map;
//	}
//	
//	/**
//	 * 找回密码
//	 * @param param
//	 * @return
//	 */
//	@RequestMapping(path="/retrievePasswordForWeb",method=RequestMethod.POST)
//	@ResponseBody
//	public Map<String,Object> retrievePasswordForWeb(@Valid @RequestBody RetrievePasswordForWebParam param){
//		Map<String,Object> map = new HashMap<String,Object>();
//		User user = userResource.getUserByUserId(param.getUserId());
//		if(null == user){
//			map.put("flag", "fail");
//			map.put("message", "该用户不存在！");
//			return map;
//		}
//		map = userPwdClient.retrievePasswordForWeb(param);
//		return map;
//	}
//	
//	/**
//	 * 验证手机号是否注册
//	 * @param orgId
//	 * @return
//	 */
//	@RequestMapping(path="/common/checkPhoneRegist/{telephone}", method={RequestMethod.GET})
//	public boolean checkPhoneRegist(@PathVariable("telephone") String telephone){
//		//如果该短信是找回密码，则需要判断这个手机号是否已经注册过了
//		User u = userResource.getUserByPhone(telephone);
//		if(u == null){
//			return false;
//		}
//		return true;
//	}
//	
//	//--------------互联网web端独有找回密码--------------------
//	
//	
//	
//	/**
//	 * 修改密码
//	 * @param username
//	 * @param newPassword
//	 * @return
//	 */
//	@RequestMapping(path="/internet/userPwdModify",method=RequestMethod.POST)
//	public Map<String,Object> userPwdModify(@Valid @RequestBody UserPwdModifyParam param){
//		Map<String, Object> res = new HashMap<String, Object>();
//		User user = userResource.getUserByUserId(param.getUserId());
//		if(null == user){
//			res.put("flag", "fail");
//			res.put("message", "用户不存在");
//			return res;
//		}
//		res = userPwdClient.retrievePassword(param);
//		return res;
//	}
//	
//	@RequestMapping(value = { "/internet/headpicUpload/{userId}/" }, method = RequestMethod.POST)
//	@ResponseBody
//	public String Upload(HttpServletRequest request,@PathVariable("userId")String userId) throws ServletException {
//		String url = userPwdClient.headpicUpload(request,userId);
//		return url;
//	}
//	
//	@RequestMapping(value = "/internet/getUserLogo/{username}/", method = RequestMethod.GET)
//	public String Upload(@PathVariable("username")String username){
//		String logo = userApp.getUserLogoByUsername(username);
//		if(!StringUtils.isNull(logo)){
//			return userLogoUrl+logo;
//		}else{
//			return defaultUserLogoUrl;
//		}
//	}
//	
//	/**
//	 * 找回密码
//	 * @param param
//	 * @return
//	 */
//	@RequestMapping(path="/retrievePassword",method=RequestMethod.POST)
//	public Map<String,Object> retrievePassword(@Valid @RequestBody RetrievePasswordParam param){
//		Map<String,Object> map = new HashMap<String,Object>();
//		User user = userApp.getUserByUserName(param.getUsername());
//		if(null == user){
//			map.put("flag", "fail");
//			map.put("message", "该用户不存在！");
//			return map;
//		}
//		if(null == user.getTelePhone()){
//			map.put("flag", "fail");
//			map.put("message", "该用户没有绑定手机!");
//			return map;
//		}
//		//校验验证码
//		map = smClient.isVerifyCodeValid(user.getTelePhone(),SendMessageConstants.RETRIEVEPASSWORD,param.getNewVericode());
//		if(null != map && map.get("flag").equals("fail")){
//			return map;
//		}
//		if(StringUtils.isNull(param.getNewVericode())){
//			map.put("flag", "fail");
//			map.put("message", "验证码错误!");
//			return map;
//		}
//		map = userPwdClient.retrievePassword(param);
//		return map;
//	}
//	
//	/**
//	 * 验证原手机号
//	 * @param param
//	 * @return
//	 */
//	@RequestMapping(path="/internet/oldphoneVerify",method=RequestMethod.POST)
//	public Map<String,Object> oldphoneVerify(@Valid @RequestBody OldphoneVerifyParam param){
//		Map<String,Object> map = new HashMap<String,Object>();
//		String oldPhone = param.getOldPhone();
//		String userId = param.getUserId();
//		String vericode = param.getVerifyCode();
//		//增加短信验证
//		map = smClient.isVerifyCodeValid(oldPhone,SendMessageConstants.OLDPHONEVERIFY,vericode);
//		if(null != map && map.get("flag").equals("fail")){
//			return map;
//		}
//		User u = userResource.getUserByUserId(userId);
//		if(null == u){
//			map.put("flag", "fail");
//			map.put("message", "该用户不存在！");
//			return map;
//		}
//		if(!oldPhone.equals(u.getTelePhone())){
//			map.put("flag", "fail");
//			map.put("message", "原手机号错误！");
//			return map;
//		}
//		map.put("flag", "success");
//		map.put("message", "验证成功");
//		return map;
//	}
//	
//	/**
//	 * 验证新手机号并绑定
//	 * @param param
//	 * @return
//	 */
//	@RequestMapping(path="/internet/bindNewphone",method=RequestMethod.POST)
//	public Map<String,Object> bindNewphone(@Valid @RequestBody BindNewphoneParam param){
//		Map<String,Object> map = new HashMap<String,Object>();
//		String newPhone = param.getNewPhone();
//		String userId = param.getUserId();
//		String vericode = param.getVerifyCode();
//		//增加短信验证
//		map = smClient.isVerifyCodeValid(newPhone,SendMessageConstants.NEWPHONEVERIFY,vericode);
//		if(null != map && map.get("flag").equals("fail")){
//			return map;
//		}
//		User u = userResource.getUserByUserId(userId);
//		if(null == u){
//			map.put("flag", "fail");
//			map.put("message", "该用户不存在！");
//			return map;
//		}
//		if(u.getTelePhone().equals(newPhone)){
//			map.put("flag", "fail");
//			map.put("message", "新手机号与原手机号相同，无需重新绑定！");
//			return map;
//		}
//		User otherUser = userResource.getUserByPhone(newPhone);
//		if(null != otherUser){
//			map.put("flag", "fail");
//			map.put("message", "该手机号已经被绑定!");
//			return map;
//		}
//		u.setTelePhone(newPhone);
//		u.setUsername(newPhone);
//		userResource.save(u);
//		map.put("flag", "success");
//		map.put("message", "绑定成功");
//		return map;
//	}
//	
//	/**
//	 * 根据用户名获取绑定手机号
//	 * @param param
//	 * @return
//	 */
//	@RequestMapping(path="/gettelephoneByusername",method=RequestMethod.POST)
//	public String gettelephoneByusername(String username){
//		String telephone = null;
//		User user = userResource.getUserByAccount(username);
//		if(null != user){
//			telephone = user.getTelePhone();
//		}
//		return telephone;
//	}
	
}
