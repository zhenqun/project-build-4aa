//package com.ido85.party.aaa.authentication.web;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.inject.Inject;
//import javax.validation.Valid;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.ido85.party.aaa.authentication.application.UserApplication;
//import com.ido85.party.aaa.authentication.dto.RetrievePasswordForWebParam;
//import com.ido85.party.aaa.authentication.dto.RetrievePasswordParam;
//import com.ido85.party.aaa.authentication.dto.UserPwdModifyParam;
//import com.ido85.party.aaa.authentication.dto.VerifyRtPwdVfCodeParam;
//import com.ido85.party.aaa.authentication.entity.User;
//import com.ido85.party.aaa.authentication.reposities.UserResources;
//import com.ido85.party.platform.utils.StringUtils;
//
//@RestController
//public class UserPwdController {
//
//	@Inject
//	private UserResources userResource;
//	
//	@Inject
//	private PasswordEncoder encoder;
//	
//	@Inject
//	private UserApplication userApp;
//	
//	//--------------互联网web端独有找回密码--------------------
//	/**
//	 * 找回密码
//	 * @param param
//	 * @return
//	 */
//	@RequestMapping(path="/internet/retrievePasswordForWeb",method=RequestMethod.POST)
//	@ResponseBody
//	public Map<String,Object> retrievePasswordForWeb(@Valid @RequestBody RetrievePasswordForWebParam param){
//		Map<String,Object> map = new HashMap<String,Object>();
//		String pwd = param.getPassword();
//		User user = userResource.getUserByUserId(param.getUserId());
//		if(null == user){
//			map.put("flag", "fail");
//			map.put("message", "该用户不存在！");
//			return map;
//		}
//		user.setPassword(encoder.encode(pwd));
//		userResource.save(user);
//		map.put("flag", "success");
//		map.put("message", "修改成功");
//		return map;
//	}
//	
//	//--------------互联网web端独有找回密码--------------------
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
//		String newpwd = param.getNewPassword();
//		String oldpwd = param.getOldPassword();
//		User user = userResource.getUserByUserId(param.getUserId());
//		if(null == user){
//			res.put("flag", "fail");
//			res.put("message", "用户不存在");
//			return res;
//		}
//		if(StringUtils.isEmpty(oldpwd) || 
//				!encoder.matches(oldpwd, user.getPassword())){
//			res.put("flag", "fail");
//			res.put("message", "旧密码错误");
//			return res;
//		}
//		if(StringUtils.isEmpty(newpwd)){
//			res.put("flag", "fail");
//			res.put("message", "新密码不能为空");
//			return res;
//		}
//		user.setPassword(encoder.encode(newpwd));
//		userResource.save(user);
//		res.put("flag", "success");
//		res.put("message", "修改成功");
//		return res;
//	}
//	
//	/**
//	 * 找回密码
//	 * @param param
//	 * @return
//	 */
//	@RequestMapping(path="/internet/retrievePassword",method=RequestMethod.POST)
//	public Map<String,Object> retrievePassword(@Valid @RequestBody RetrievePasswordParam param){
//		Map<String,Object> map = new HashMap<String,Object>();
//		String pwd = param.getNewPassword();
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
//		if(StringUtils.isNull(param.getNewVericode())){
//			map.put("flag", "fail");
//			map.put("message", "验证码错误!");
//			return map;
//		}
//		user.setPassword(encoder.encode(pwd));
//		userResource.save(user);
//		map.put("flag", "success");
//		map.put("message", "修改成功");
//		return map;
//	}
//}
