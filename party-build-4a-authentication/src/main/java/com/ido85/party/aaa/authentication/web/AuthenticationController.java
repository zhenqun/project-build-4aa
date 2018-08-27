package com.ido85.party.aaa.authentication.web;

import com.ido85.party.aaa.authentication.application.Authentication;
import com.ido85.party.aaa.authentication.application.UserApplication;
import com.ido85.party.aaa.authentication.dto.*;
import com.ido85.party.aaa.authentication.entity.AuthenticationInfo;
import com.ido85.party.aaa.authentication.entity.Role;
import com.ido85.party.aaa.authentication.entity.User;
import com.ido85.party.aaa.authentication.ncii.client.NciicClient;
import com.ido85.party.aaa.authentication.reposities.AuthInfoResource;
import com.ido85.party.aaa.authentication.reposities.RoleResource;
import com.ido85.party.aaa.authentication.reposities.UserResources;
import com.ido85.party.aaa.authentication.reposities.UserRoleResource;
import com.ido85.party.platform.distribute.generator.IdGenerator;
import com.ido85.party.platform.utils.StringReplaceUtils;
import com.ido85.party.platform.utils.StringUtils;
import com.ido85.party.platform.utils.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class AuthenticationController {

	@Inject
	private IdGenerator idGenerator;
	
	@Inject
	private AuthInfoResource authInfoResource;
	
	@Inject
	private RoleResource roleResource;
	
	@Inject
	private Authentication authentication;

	@Inject
	private UserRoleResource userRoleResource;

	@Inject
	private UserApplication userApplication;

	@Inject
	private UserResources userResources;

	@RequestMapping(value = "/authentication/insertAuthentiInfo",method = RequestMethod.POST)
	private Map<String,String> insertAuthentiInfo(@Valid @RequestBody List<AuthentiInfoDto> param) {
		Map<String,String> map = new HashMap<>();
		String clientId = null;
		String clientName = null;
		String relName = null;
		Long roleId = null;
		String userId = null;
		String uniqueKey = null;
		String idCard = null;
		List<AuthenticationInfo> list = null;
		AuthenticationInfo info = null;
		if(param != null && param.size() > 0){
			list = new ArrayList<>();
			for(AuthentiInfoDto dto:param){
				clientId = dto.getClientId();
				clientName = dto.getClientName();
				relName = dto.getRelName();
				roleId = dto.getRoleId();
				uniqueKey = dto.getUniqueKey();
				idCard = dto.getIdCard();
				userId = dto.getUserId();
//				Id = dto.getUserId();
				//校验是否重复
				info = authInfoResource.getInfoByAuthenti(clientId,relName,roleId,uniqueKey);
				if(null!=info){
					map.put("flag", "fail");
					map.put("message", "已经上传过该用户认证信息");
					return map;
				}
				info = new AuthenticationInfo();
				info.setAuthenticationInfoId(idGenerator.next());
				info.setClientId(clientId);
				info.setClientName(clientName);
				info.setCreateDate(new Date());
				info.setDelFlag("0");
				info.setRelName(relName);
				info.setRoleId(roleId);
				info.setUniqueKey(uniqueKey);
				info.setUserId(userId);
				info.setValidFlag("0");
				info.setIdCard(idCard);
				list.add(info);
			}
			authInfoResource.save(list);
			map.put("flag", "success");
			map.put("message", "添加成功!");
			return map;
		}
		map.put("flag", "fail");
		map.put("message", "请填写认证信息");
		return map;
	}
	
	@RequestMapping(value = "/authentication/authentiInfoQuery",method = RequestMethod.POST)
	private List<RoleDto> authentiInfoQuery(@Valid @RequestBody AuthentiInfoParam param) {
		List<RoleDto> list = authentication.authentiInfoQuery(param.getUniqueKey(),param.getRelName());
		return list;
	}
	
	
	@RequestMapping(value = "/authentication/roleQuery/{clientId}",method = RequestMethod.GET)
	private List<String> roleQuery(@PathVariable("clientId") String clientId) {
		return roleResource.getRoleById(clientId);
	}
	
//	@RequestMapping(value = "/authentication/roleAdd",method = RequestMethod.POST)
//	private boolean roleAdd(@Valid @RequestBody List<RoleAddDto> dtoList) {
//		List<Role> list = null;
//		Role role = null;
//		if(null != dtoList && dtoList.size() > 0){
//			list = new ArrayList<Role>();
//			for(RoleAddDto dto:dtoList){
//				if(null != dto){
//					role = new Role();
//					role.setClientId(dto.getClientId());
//					role.setDescription(dto.getDescription());
//					role.setId(idGenerator.next());
//					role.setName(dto.getRoleName());
//					list.add(role);
//				}
//			}
//			roleResource.save(list);
//			return true;
//		}
//		return false;
//	}
	
	/**
	 * 实名认证服务
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/authentication/realnameAuthentication",method = RequestMethod.POST)
	private Map<String,Object> realnameAuthentication(@Valid @RequestBody RealnameAuthenticationParam param) {
		String idCard = param.getIdCard();
		String name = param.getName();
		Map<String,Object> map = new HashMap<>();
		try{
			map = authentication.nciicAuthentication(idCard, name);
			return map;
		}catch (Exception e){
			map.put("flag", "exception");
			map.put("message", "实名认证服务异常!");
			return map;
		}
	}

	/**
	 * 批量实名认证
	 * @return
	 */
	@RequestMapping(value = "/authentication/realnameAuthenticationBatch",method = RequestMethod.POST)
	private List<AuthenticatResultDto> realnameAuthenticationBatch(@Valid @RequestBody AuthenticationBatchParam in) throws Exception{
		String param = in.getParam();
		List<AuthenticatResultDto> dtoList = new ArrayList<>();
		AuthenticatResultDto dto = null;
		NciicClient nciicClient = new NciicClient();
		String result = null;
		boolean nciisCheckResult = false;
		String parseStr = null;
		String[] keys = param.split(",");
//		System.out.println(keys);
		String[] idCardName = null;
		String idCard = null;
		String name = null;
		Map<String,Object> map = null;
		for(String key:keys){
			System.out.println(key);
			dto = new AuthenticatResultDto();
			idCardName = key.split("@");
			System.out.println(idCardName);
			idCard = idCardName[0];
			name = idCardName[1];
			System.out.println(idCard);
			System.out.println(name);
			String inConditions = "<?xml version='1.0' encoding='utf-8'?>\n" + "<ROWS>\n" + "<INFO>\n"
					+ "<SBM>八五创新</SBM>\n" + "</INFO>\n"
					+ "<ROW>\n"
					+ "<GMSFHM>公民身份号码</GMSFHM>\n"
					+ "<XM>姓名</XM>\n"
					+ "</ROW>\n"
					+ "<ROW FSD='370000' YWLX='党员认证'>\n"
					+ "<GMSFHM>"+idCard+"</GMSFHM>\n"
					+ "<XM>"+name+"</XM>\n" + "</ROW>\n" + "</ROWS>";
			result = nciicClient.executeClient(inConditions);
			System.out.println(result);
			parseStr = XmlUtils.parseXml(result);
			if("一致一致".equals(parseStr)){
				nciisCheckResult = true;
				dto.setFlag(true);
			}else{
				dto.setFlag(false);
			}
			dto.setKey(idCardName.toString());
			dto.setResult(result);
			dtoList.add(dto);
		}
		return dtoList;
	}

	/**
	 * 删除用户身份认证
	 * @return
	 */
	@RequestMapping(value = "/authentication/deleteAuthentication",method = RequestMethod.POST)
	private boolean deleteAuthentication(@Valid @RequestBody List<DeleteAuthenticationParam> in){
		String clientId = null;
		Long roleId = null;
		String userId = null;
		List<AuthenticationInfo> aiList = null;
//		List<AuthenticationInfo> list = null;
		List<String> userIds = null;
		//查询所有认证消息
		if(null != in && in.size() > 0){
//			list = new ArrayList<>();
			userIds = new ArrayList<>();
			for(DeleteAuthenticationParam param:in){
				clientId = param.getClientId();
				roleId = param.getRoleId();
				userId = param.getUserId();
				aiList = authInfoResource.getInfoByCRU(clientId,roleId,userId);
				if(null != aiList && aiList.size() > 1){
//					list.add(ai);
					for(AuthenticationInfo ai:aiList){
						userIds.add(ai.getUserId());
					}
				}
			}
		}
		//将所有认证信息的valide_flag 改为1
		if(null != aiList && aiList.size() > 0) {
			for (AuthenticationInfo auth : aiList) {
				if (null != auth) {
					auth.setValidFlag("1");
				}
			}
		}
		if(null != aiList && aiList.size() > 0){
			authInfoResource.save(aiList);
		}
		if(null != userIds && userIds.size() > 0){
			//去除这些账号的对应的角色
			userRoleResource.deleteUserRole(userIds,new Date());
		}
		return true;
	}

	/**
	 * 批量添加认证信息
	 * @param param
	 * @return
     */
	@RequestMapping(value = "/authentication/insertAuthentiInfoBatch",method = RequestMethod.POST)
	private boolean insertAuthentiInfoBatch(@Valid @RequestBody List<AuthentiInfoDto> param) {
		String clientId = null;
		String clientName = null;
		String relName = null;
		Long roleId = null;
		String userId = null;
		String uniqueKey = null;
		String idCard = null;
		List<AuthenticationInfo> list = null;
		AuthenticationInfo info = null;
		try {
			if (param != null && param.size() > 0) {
				list = new ArrayList<>();
				for (AuthentiInfoDto dto : param) {
					clientId = dto.getClientId();
					clientName = dto.getClientName();
					relName = dto.getRelName();
					roleId = dto.getRoleId();
					uniqueKey = dto.getUniqueKey();
					idCard = dto.getIdCard();
					userId = dto.getUserId();
//				Id = dto.getUserId();
					//校验是否重复
					info = authInfoResource.getInfoByAuthenti(clientId, relName, roleId, uniqueKey);
					if (null == info) {
						info = new AuthenticationInfo();
						info.setAuthenticationInfoId(idGenerator.next());
						info.setClientId(clientId);
						info.setClientName(clientName);
						info.setCreateDate(new Date());
						info.setDelFlag("0");
						info.setRelName(relName);
						info.setRoleId(roleId);
						info.setUniqueKey(uniqueKey);
						info.setUserId(userId);
						info.setValidFlag("0");
						info.setIdCard(idCard);
						list.add(info);
					}
				}
				authInfoResource.save(list);
			}
		} catch (Exception e){
			return false;
		}
		return true;
	}
    /**
     * 认证信息同步
     * @return
     */
    @RequestMapping(value = "/authentication/authentiInfoAysc",method = RequestMethod.POST)
    private boolean authentiInfoAysc(@Valid @RequestBody List<AuthentiInfoAyscDto> dtoList) {
		log.info("认证信息同步开始===============数据量:"+dtoList.size());
		//获取所有userid
		Long addCnt = 0L;
		Long modCnt = 0L;
		Long userCnt = 0L;
		boolean flag = false;
		boolean userFlag = false;
        List<String> userIds = null;
        List<AuthenticationInfo> auList = null;
        List<UserAyscDto> userList = null;
        List<AuthenticationInfo> result = new ArrayList<>();
        List<User> userResult = new ArrayList<>();
		AuthenticationInfo authenticationInfo = null;
		User user = null;
		String hash = null;
		String idCardHash = null;
		String name = null;
		String roleId = null;
		UserAyscDto userAyscDto = null;
        if(null != dtoList && dtoList.size() > 0){
            userIds = new ArrayList<>();
            for(AuthentiInfoAyscDto dto:dtoList){
                userIds.add(dto.getUserId());
            }
            roleId = dtoList.get(0).getRoleId();
            //根据userId获取所有认证信息
			auList = authInfoResource.getInfoByUserIds(userIds);
			//获取所有这些认证信息的账号
			userList = userApplication.getUserByAuthUserIds(userIds,roleId);
        }
		log.info("认证库数据查询出"+auList.size()+"===============");
			//比对两组信息
			for (AuthentiInfoAyscDto dto:dtoList) {
				int index = auList.indexOf(dto);
				if(-1 == index){
					//新增
					authenticationInfo = new AuthenticationInfo();
					authenticationInfo.setAuthenticationInfoId(idGenerator.next());
					authenticationInfo.setClientId(dto.getClientId());
					authenticationInfo.setCreateDate(new Date());
					authenticationInfo.setDelFlag("0");
					authenticationInfo.setIdCard(dto.getIdCard());
					authenticationInfo.setRelName(dto.getRelName());
					authenticationInfo.setRoleId(Long.parseLong(dto.getRoleId()));
					authenticationInfo.setUniqueKey(dto.getIdCard());
					authenticationInfo.setUserId(dto.getUserId());
					authenticationInfo.setValidFlag(dto.getValidFlag());
					result.add(authenticationInfo);
					addCnt = addCnt+1L;
				}else{
					authenticationInfo = auList.get(index);
					if(!authenticationInfo.getIdCard().equals(dto.getIdCard())){
						authenticationInfo.setIdCard(dto.getIdCard());
						flag = true;
					}
					if(!authenticationInfo.getRelName().equals(dto.getRelName())){
						authenticationInfo.setRelName(dto.getRelName());
						flag = true;
					}
					if(!authenticationInfo.getUniqueKey().equals(dto.getIdCard())){
						authenticationInfo.setUniqueKey(dto.getIdCard());
						flag = true;
					}
					if(!StringUtils.toString(authenticationInfo.getValidFlag()).equals(dto.getValidFlag())){
						authenticationInfo.setValidFlag(dto.getValidFlag());
						flag = true;
					}
					if(flag){
						modCnt = modCnt + 1L;
					}
					result.add(authenticationInfo);
				}
				//更新账号的hash idcardHash name
				int userIndex = userList.indexOf(dto);
				if(-1 != userIndex){
					user = userList.get(userIndex).getUser();
					userAyscDto = userList.get(userIndex);
					hash = StringUtils.getUserNameIDHash(dto.getRelName(),dto.getIdCard());
					idCardHash = StringUtils.getIDHash(dto.getIdCard());
					name = StringReplaceUtils.getStarString(dto.getRelName());
					if(!hash.equals(user.getHash())){
						user.setHash(hash);
						userFlag = true;
					}
					if(!idCardHash.equals(user.getIdCardHash())){
						user.setIdCardHash(idCardHash);
						userFlag = true;
					}
					if(!name.equals(user.getName())){
						user.setName(name);
						userFlag = true;
					}
					if(userFlag){
						userCnt = userCnt + 1;
					}
					userResult.add(user);
				}
			}
			try{
				authInfoResource.save(result);
				userResources.save(userResult);
				log.info("认证库数据新增"+addCnt+"===============,修改:"+modCnt);
				log.info("账号更新"+userCnt+"条===============");
			}catch (Exception e){
				return false;
			}
		return true;
	}
}
