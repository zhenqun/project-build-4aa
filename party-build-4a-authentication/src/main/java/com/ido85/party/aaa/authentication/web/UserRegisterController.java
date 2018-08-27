//package com.ido85.party.aaa.authentication.web;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.inject.Inject;
//import javax.transaction.Transactional;
//import javax.validation.Valid;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.ido85.party.aaa.authentication.application.Authentication;
//import com.ido85.party.aaa.authentication.application.UserApplication;
//import com.ido85.party.aaa.authentication.dto.AuthentiInfoParam;
//import com.ido85.party.aaa.authentication.dto.InBaseUserDto;
//import com.ido85.party.aaa.authentication.dto.RoleDto;
//import com.ido85.party.aaa.authentication.entity.User;
//import com.ido85.party.aaa.authentication.entity.UserRole;
//import com.ido85.party.aaa.authentication.reposities.UserRoleResource;
//import com.ido85.party.platform.distribute.generator.IdGenerator;
//import com.ido85.party.platform.utils.StringReplaceUtils;
//import com.ido85.party.platform.utils.StringUtils;
//
//import lombok.extern.slf4j.Slf4j;
//
//
//@RestController
//@Slf4j
//public class UserRegisterController {
//	
//	@Inject
//	private UserApplication userApp;
//	
//	@Inject
//	private IdGenerator idGenerator;
//	
//	@Inject
//	private PasswordEncoder encoder;
//	
//	@Value("${checkUserExistUrl}")
//	private String checkUserExistUrl;
//	
//	@Value("${queryUserInfoByHashUrl}")
//	private String queryUserInfoByHashUrl;
//	
//	@Inject
//	private Authentication authentication;
//	
//	@Inject
//	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
//	
//	@Inject
//	private UserRoleResource userRoleResource;
//	
//	@RequestMapping(path="/internet/register", method=RequestMethod.POST)
//	@Transactional
//	public Map<String, Object> register(@RequestBody @Valid InBaseUserDto dto) throws Exception{
//		String telephone = dto.getTelePhone();
//		String name = dto.getName();
//		String password = dto.getPassword();
//		String idCard =dto.getIdCard();
//		List<String> uniqueKeys = dto.getUniqueKey();
//		Map<String, Object> param = new HashMap<>();
//		User user = userApp.getUserByLoginName(telephone,idCard);
//		if(null != user){
//			threadPoolTaskExecutor.execute(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						//调用日志服务
//						//registerLogApplication.addRegisterLog(request,username,null,null,null,name,idCard,null,"用户已经存在!",LogConstants.REGISTER_FAIL,"注册失败");
//					} catch (Exception e) {
//						log.error(e.getMessage());
//					}
//				}
//			});
//			param.put("flag", "fail");
//			param.put("message", "用户已经存在!");
//			return param;
//		}
//		//实名认证
//		Map<String,Object> result = authentication.nciicAuthentication(idCard,name);
//		if(result.containsKey("flag") && result.get("flag").equals("fail")){
//			param.put("flag", "fail");
//			param.put("message", "实名认证失败!");
//			return param;
//		}
//		//调用认证服务 如果不存在任何认证 则注册失败
//		List<RoleDto> roleList = authentication.authentiInfoQuery(uniqueKeys,name);
//		if(null == roleList){
//			//增加日志 调用日志服务
//			threadPoolTaskExecutor.execute(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						//调用日志服务
////				registerLogApplication.addRegisterLog(request,username,null,null,null,name,idCard,null,"无法确定党员身份",LogConstants.REGISTER_FAIL,"注册失败");
//					} catch (Exception e) {
//						log.error(e.getMessage());
//					}
//				}
//			});
//			param.put("flag", "fail");
//			param.put("message", "系统暂时无法确认您的身份，请联系所在党支部、单位等进行身份确认");
//			return param;
//		}
//		userRegister(name,password,telephone,idCard,uniqueKeys,roleList);
//		param.put("flag", "success");
//		param.put("message", "注册成功!");
//		return param;
//	}
//	
//	private void userRegister(String name, String password, String telephone, String idCard,List<String> uniqueKeys,List<RoleDto> roleList) throws Exception {
//		String id = StringUtils.toString(idGenerator.next());
//		String hash = StringUtils.getIDHash(idCard);
//		String nameIdcardhash = StringUtils.getUserNameIDHash(name, idCard);
//		User newUser = new User();
//		newUser.setDisabled(true);
//		newUser.setName(StringReplaceUtils.getStarString(name));
//		newUser.setPassword(encoder.encode(password));
//		newUser.setCreateDate(new Date());
//		newUser.setTelePhone(telephone);
//		newUser.setAccountExpired(false);
//		newUser.setAccountLocked(false);
//		newUser.setEnabled(false);
//		newUser.setDisabled(false);
//		newUser.setPwdExpired(false);
//		newUser.setUsername(telephone);//用户账号
//		newUser.setHash(nameIdcardhash);
//		newUser.setId(id);
//		newUser.setIdCardHash(hash);
//		userApp.insertUser(newUser);
//		AuthentiInfoParam param = new AuthentiInfoParam();
//		param.setRelName(name);
//		param.setUniqueKey(uniqueKeys);
//		List<UserRole> userRoleList = null;
//		UserRole userRole = null;
//		if(null != roleList && roleList.size() > 0){
//			userRoleList = new ArrayList<UserRole>();
//			for(RoleDto roleDto:roleList){
//				userRole = new UserRole();
//				userRole.setRelId(idGenerator.next());
//				userRole.setRoleId(roleDto.getRoleId());
//				userRole.setUserId(roleDto.getUserId());
//				userRoleList.add(userRole);
//			}
//			userRoleResource.save(userRoleList);
//		}
////			registerLogApplication.addRegisterLog(request, username, orgName, orgId,orgCode, name, idCard, userId,"注册成功",LogConstants.REGISTER_SUCCESS,"注册成功");
//		// 记录日志
////		registerLogApplication.addRegisterLog(request, username, orgName, orgId,orgCode, name, idCard, userId,"注册失败",LogConstants.REGISTER_SUCCESS,"注册失败");
//	}
//	
//}
