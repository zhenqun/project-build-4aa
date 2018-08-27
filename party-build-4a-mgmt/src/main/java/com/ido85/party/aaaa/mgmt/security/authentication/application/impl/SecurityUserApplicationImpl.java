package com.ido85.party.aaaa.mgmt.security.authentication.application.impl;

import com.ido85.party.aaaa.mgmt.business.application.ClientApplication;
import com.ido85.party.aaaa.mgmt.business.dto.ExportAuthorizationCodeParam;
import com.ido85.party.aaaa.mgmt.business.dto.ExportAuthorizationCodeVo;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.base.ConfigEnum;
import com.ido85.party.aaaa.mgmt.ldap.application.LdapApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.*;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.*;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.*;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.*;
import com.ido85.party.aaaa.mgmt.security.common.LogUtils;
import com.ido85.party.aaaa.mgmt.security.common.UserUtils;
import com.ido85.party.aaaa.mgmt.security.constants.LogConstants;
import com.ido85.party.aaaa.mgmt.security.utils.*;
import com.ido85.party.aaaa.mgmt.security.utils.excel.ExportCompareExcel;
import com.ido85.party.aaaa.mgmt.service.RealnameAuthenticaitonClient;
import com.ido85.party.aaaa.mgmt.service.SimpleDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thymeleaf.util.ListUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.*;
import java.util.*;

//import com.ido85.party.aaaa.mgmt.security.authentication.domain.ClientUserRel;
//import com.ido85.party.aaaa.mgmt.security.authentication.repository.ClientMainUserRelResources;

/**
 * Created by fire on 2017/2/22.
 */
@Named
@Slf4j
public class SecurityUserApplicationImpl implements SecurityUserApplication {
	@Inject
	private UserResources userRes;
	
	@Inject
	private UserApplication userApp;
	
	@Inject
	private RealnameAuthenticaitonClient realnameAuthenticaitonClient;

	@Value("${ROLE_SECURITY}")
	private String roleSecurity;

	@Value("${ROLE_AUDITOR}")
	private String roleAuditor;
	
	@Inject
	private RoleResources roleResources;

	@PersistenceContext(unitName = "admin")
	private EntityManager adminEntity;
	
	@Inject
	private ConfigApplication configApp;
	
	@Inject
	private GrantLogApplication grantLogApplication;
	
	@Inject
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	@Value("${downUrl}")
	private String downUrl;
	
	@Inject
	private CommonApplication commonApplication;
	
	@Inject
	private IdGenerator idGenerator;

	@Inject
	private DelSecurityUserResource delSecurityUserResource;
	
//	@Inject
//	private ClientMainUserRelResources clientUserRelResources;
	
	@Inject
	private ClientApplication clientApplication;
	
	@Inject
	private UserClientRelResource userClientRelResource;
	
	@Inject
	private UserVpnRelResources userVpnRelResources;

	@Inject
	private LdapApplication ldapApplication;

	@Inject
	private SimpleDataService simpleDataService;

	@Inject
	private AssistCountyResources assistCountyResources;

	@Override
	@Transactional
	public Map<String, String> addSecurityUser(List<InAddSecurityUserDto> param,HttpServletRequest request) throws Exception {
//		User currentUser = UserUtils.getCurrentUser();
		Map<String,String> result = new HashMap<>();
		//参数检测
		result = checkParam(param);
		if(result.containsKey("flag") && result.get("flag").equals("fail")){
			result.put("message","数据处理异常!");
			return result;
		}
		if(result.containsKey("flag") && result.get("flag").equals("check")){
			return result;
		}
		StringBuffer message = new StringBuffer();
		List<User> entities = new ArrayList<>();
//		List<ClientUserRel> rels = new ArrayList<ClientUserRel>();
		UserClientRel clientUserRel = null;
		String clientId = null;
		UserVpnRel userVpnRel = null;
		SetSecurityUserManageScopeParam scope = null;
		User createOr = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(ListUntils.isNotNull(param)){
			User user = null;
			Set<Role> roles;
			Role role;
			for(InAddSecurityUserDto userDto : param){
				//检测改组织在该应用下是否有安全员
				Long cnt = this.checkOrgSecurityUserCnt(userDto.getClientId(),userDto.getScope().getManageOrgId());
				if(cnt.longValue() > 0L){
					result.put("flag","fail");
					result.put("message","该组织下已经有安全员!");
					return result;
				}
				String hash = StringUtils.getUserNameIDHash(userDto.getRelName(),userDto.getIdCard());
				//判断用户是不是业务管理员，不能共存
				boolean isBusi = commonApplication.checkIsBusinessUser(hash);
				if(isBusi){
					result.put("flag", "fail");
					result.put("message", "此用户已经为管理员，不能授予安全员角色!");
					return result;
				}
				User u = userApp.getUserByHash(hash);
				if(null != u){
					//判断有没有审计员角色，审计员和安全员不共存
					role = roleResources.getRoleByName(roleAuditor);
					roles = u.getRoles();
					if (null == role || role.getId() < 0) {
						result.put("flag", "fail");
						result.put("message", "角色不存在!");
						return result;
					}
					if(roles.contains(role)){
						result.put("flag", "fail");
						result.put("message", "此用户已经为审计员，不能授予安全员角色!");
						return result;
					}
					clientId = userDto.getClientId();
					List<UserClientRel> clientRels = userClientRelResource.getRelByUserIdClientId(u.getId(),clientId);
					if(ListUntils.isNotNull(clientRels)){
						result.put("flag", "fail");
						result.put("message", "该用户已经有当前应用的管理权限!");
						return result;
					}
					scope = userDto.getScope();
					clientUserRel = new UserClientRel();
					clientUserRel.setClientId(clientId);
					clientUserRel.setId(idGenerator.next());
					clientUserRel.setUserId(u.getId());
					clientUserRel.setManageId(scope.getManageOrgId());
					clientUserRel.setManageName(scope.getManageOrgName());
					clientUserRel.setManageCode(scope.getManageOrgCode());
					//需要检测授权是否合法
//					boolean checkFlag = commonApplication.checkSecurityUserOu(u.getId(), scope.getOuName());
//					if(!checkFlag){
//						result.put("flag", "fail");
//						result.put("message", "所选管理范围和用户已有管理范围不在同一节点");
//						return result;
//					}
//					List<ClientUserRel> lists = clientUserRelResources.getCurByClientUser(clientId,u.getId());
//					if(ListUntils.isNull(lists)){
//						rels.add(clientUserRel);
//					}
					userClientRelResource.save(clientUserRel);
					message.append(u.getId()).append("|").append(u.getName()).append(",");
					role = roleResources.getRoleByName(roleSecurity);

					if (null == role || role.getId() < 0) {
						result.put("flag", "fail");
						result.put("message", "角色不存在!");
						return result;
					}
					if(!roles.contains(role)){
						roles = new HashSet<>();
						roles.add(role);
						u.setRoles(roles);
						entities.add(u);
					}
				}else{
					result = realnameAuthenticaitonClient.realnameAuthentication(userDto.getIdCard(),userDto.getRelName());
					if(null != result && result.containsKey("flag")){
						if("fail".equals(result.get("flag")) || "exception".equals(result.get("flag"))){
							return result;
						}
					}
					user = new User();
					String userId = StringUtils.toString(idGenerator.next());
					String code = StringUtils.getRandomNum(8);
					user.setAccountExpired(false);
					user.setAccountLocked(false);
					user.setPwdExpired(false);
					user.setActivation(false);
					user.setAuthorizationCode(code);
					user.setCreateDate(new Date());
					user.setDisabled(false);
					user.setEnabled(true);
					user.setHash(hash);
					user.setIdCard(userDto.getIdCard());
					user.setName(userDto.getRelName());
					user.setId(userId);
					user.setTelePhone(userDto.getTelephone());
					if(null != createOr){
						user.setCreateBy(createOr.getId());
					}
					entities.add(user);
					scope = userDto.getScope();
					clientId = userDto.getClientId();
					clientUserRel = new UserClientRel();
					clientUserRel.setClientId(clientId);
					clientUserRel.setId(idGenerator.next());
					clientUserRel.setUserId(userId);
					clientUserRel.setManageId(scope.getManageOrgId());
					clientUserRel.setManageName(scope.getManageOrgName());
					clientUserRel.setManageCode(scope.getManageOrgCode());
					userClientRelResource.save(clientUserRel);
					message.append(StringUtils.toString(userId)).append("|").append(userDto.getRelName()).append(",");
					
					role = roleResources.getRoleByName(roleSecurity);

					if (null == role || role.getId() < 0) {
						result.put("flag", "fail");
						result.put("message", "角色不存在!");
						return result;
					}
					roles = new HashSet<>();
					roles.add(role);
					user.setRoles(roles);
					//String userId,String code,String phone,String idCard,String ou
					boolean vpnFlag = commonApplication.createVPNForSecurity(userId,code,userDto.getTelephone(),userDto.getIdCard(),scope.getOuName(),"2005");
				}
			}
			userRes.save(entities);
		}
		result.put("flag", "success");
		result.put("message", message.toString().substring(0, message.length()-1));
		return result;
	}

	/**
	 *  检测该组织在该应用下安全员数量
	 * @param clientId
	 * @param manageOrgId
	 * @return
	 */
	private Long checkOrgSecurityUserCnt(String clientId, String manageOrgId) {
		StringBuffer sb = new StringBuffer("select count(*) from t_4a_actors t,r_4a_user_client l,r_4a_user_role r where " +
				"t.id=l.user_id and t.id=r.user_id and l.client_id = :clientId and l.manage_id = :manageId and r.role_id = '4'");
		Query q = adminEntity.createNativeQuery(sb.toString());
		q.setParameter("clientId",clientId);
		q.setParameter("manageId",manageOrgId);
		Long cnt = StringUtils.toLong(q.getSingleResult());
		return cnt;
	}

	private Map<String,String> checkParam(List<InAddSecurityUserDto> param) {
		Map<String,String> result = new HashMap<>();
		String clientId = null;
		SetSecurityUserManageScopeParam scope = null;
		String manageId = null;
		String manageCode = null;
		String manageName = null;
		String ou = null;
		for(InAddSecurityUserDto u:param){
			clientId = u.getClientId();
			if(StringUtils.isNull(clientId)){
				result.put("flag", "fail");
				return result;
			}
			scope = u.getScope();
			if(simpleDataService.checkOrgInWhole(scope.getManageOrgId())){
				result.put("flag", "check");
				result.put("message",scope.getManageOrgName()+"正在进行组织关系整建制转接");
				return result;
			}
			manageId = scope.getManageOrgId();
			manageCode = scope.getManageOrgCode();
			manageName = scope.getManageOrgName();
			ou = scope.getOuName();
			if(StringUtils.isNull(manageId)){
				result.put("flag", "fail");
				return result;
			}
			if(StringUtils.isNull(manageCode)){
				result.put("flag", "fail");
				return result;
			}
			if(StringUtils.isNull(manageName)){
				result.put("flag", "fail");
				return result;
			}
			if(StringUtils.isNull(ou)){
				result.put("flag", "fail");
				return result;
			}
		}
		result.put("flag","success");
		return result;
	}

	/**
	 * 下级安全员查询
	 */
	public List<SecurityUserDto> securityUserQuery(SecurityUserQueryParam param) {
		//参数
		String securityName = param.getSecurityName();
		String clientId = param.getClientId();
		String state = param.getState();
		Integer pageNo = param.getPageNo();
		Integer pageSize = param.getPageSize();
		String manageId = param.getManageOrgId();
		String isActivation = param.getIsActivation();
		String usermanageId = null;
		List<SecurityUserDto> dtoList = null;
//				List<String> userIds = null;
		User user = null;
		user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(null != user){
			//查询该用户该应用的管理范围
			usermanageId = userClientRelResource.getManageIdByUserIdClientId(user.getId(),clientId);
		}
		if(StringUtils.isNull(usermanageId)){
			return null;
		}
		//根据clientid查询分级管理的地址,根据地址获取所有下级id
		List<String> manageIds = new ArrayList<>();
		if(!StringUtils.isNull(manageId)){
			/**
			 * 判断 如果用户选择的范围和本身管理范围不一致，说明选择了下级
			 * 此时需要只查询选择范围的 不能查看此级别在往下的
			 */
			if(!usermanageId.equals(manageId)){
				manageIds.add(manageId);
			}else{
				manageIds = clientApplication.getChildManageIds(clientId,manageId);
			}
		}
		StringBuffer sb = new StringBuffer("SELECT\n" +
				"	u.id,\n" +
				"	u.name,\n" +
				"	u.authorization_code,\n" +
				"	u.id_card,\n" +
				"	u.is_account_locked,\n" +
				"	u.tele_phone,\n" +
				"	u.create_date,\n" +
				"   cur.manage_id,\n" +
				"   cur.manage_name,\n" +
				"   u.is_activation\n" +
				"FROM\n" +
				"	t_4a_actors u\n");
				sb.append(
						"LEFT JOIN r_4a_user_role ur ON u.id = ur.user_id \n");
				sb.append(
						"left join r_4a_user_client cur on u.id=cur.user_id WHERE\n" +
						"	u.is_account_expired = 'f' and cur.client_id = :clientId and ur.role_id=4 and cur.manage_id != :usermanageId ");
		if(!StringUtils.isNull(securityName)){
			sb.append(" and u.name like :name ");
		}
		if(!StringUtils.isNull(state)){
			sb.append(" and u.disabled = :state ");
		}
		if(ListUntils.isNotNull(manageIds)){
			sb.append(" and cur.manage_id in :manageIds ");
		}
		if(!StringUtils.isNull(isActivation)){
			sb.append(" and u.is_activation = :isActivation");
		}
		sb.append(" order by u.create_date desc");
		Query query = adminEntity.createNativeQuery(sb.toString());
		if(!StringUtils.isNull(securityName)){
			query.setParameter("name", "%"+securityName+"%");
		}
		if(!StringUtils.isNull(state)){
			boolean stateFlag = false;
			if("1".equals(state)){
				stateFlag = true;
			}
			if("0".equals(state)){
				stateFlag = false;
			}
			query.setParameter("state", stateFlag);
		}
		if(!StringUtils.isNull(isActivation)){
			boolean stateFlag = false;
			if("1".equals(isActivation)){
				stateFlag = true;
			}
			if("0".equals(isActivation)){
				stateFlag = false;
			}
			query.setParameter("isActivation", stateFlag);
		}
		if(ListUntils.isNotNull(manageIds)){
			query.setParameter("manageIds", manageIds);
		}
		query.setParameter("usermanageId", usermanageId);
		query.setParameter("clientId", clientId);
		query.setFirstResult((pageNo - 1) * pageSize);
		query.setMaxResults(pageSize);
		List<Object[]> objectList = query.getResultList();
		if(null != objectList && objectList.size()>0){
			dtoList = new ArrayList<SecurityUserDto>();
//					userIds = new ArrayList<String>();
			SecurityUserDto dto = null;
			for(Object[] object:objectList){
				dto = new SecurityUserDto();
				dto.setAuthorizationCode(StringUtils.toString(object[2]));
				dto.setSecurityUserId(StringUtils.toString(object[0]));
				dto.setSecurityUserName(StringUtils.toString(object[1]));
				dto.setIdCard(StringUtils.toString(object[3]));
				dto.setState(StringUtils.toString(object[4]).equals("false")?"0":"1");
				dto.setTelephone(StringUtils.toString(object[5]));
				dto.setCreateDate(StringUtils.toString(object[6]));
				dto.setManageId(StringUtils.toString(object[7]));
				dto.setManageName(StringUtils.toString(object[8]));
				dto.setIsActivation(StringUtils.toString(object[9]).equals("false")?"0":"1");
				dtoList.add(dto);
//						userIds.add(StringUtils.toString(object[0]));
			}
		}
		return dtoList;	
	}

	/**
	 * 下级安全员数量查询
	 */
	public Long securityUserQueryCnt(SecurityUserQueryParam param) {
		//参数
		String securityName = param.getSecurityName();
		String clientId = param.getClientId();
		String state = param.getState();
		String manageId = param.getManageOrgId();
		String usermanageId = null;
		String isActivation = param.getIsActivation();
		List<SecurityUserDto> dtoList = null;
//				List<String> userIds = null;
		User user = null;
		user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(null != user){
			//查询该用户该应用的管理范围
			usermanageId = userClientRelResource.getManageIdByUserIdClientId(user.getId(),clientId);
		}
		if(StringUtils.isNull(usermanageId)){
			return null;
		}
		//根据clientid查询分级管理的地址,根据地址获取所有下级id
		List<String> manageIds = new ArrayList<>();
		if(!StringUtils.isNull(manageId)){
			/**
			 * 判断 如果用户选择的范围和本身管理范围不一致，说明选择了下级
			 * 此时需要只查询选择范围的 不能查看此级别在往下的
			 */
			if(!usermanageId.equals(manageId)){
				manageIds.add(manageId);
			}else{
				manageIds = clientApplication.getChildManageIds(clientId,manageId);
			}
		}
		StringBuffer sb = new StringBuffer("SELECT\n" +
				"	count(u.\"id\")\n" +
				"FROM\n" +
				"	t_4a_actors u\n");
				sb.append(
						"LEFT JOIN r_4a_user_role ur ON u.\"id\" = ur.user_id \n");
				sb.append(
						"left join r_4a_user_client cur on u.id=cur.user_id WHERE\n" +
						"	u.is_account_expired = 'f' and cur.client_id = :clientId and ur.role_id=4 and cur.manage_id != :usermanageId ");
		if(!StringUtils.isNull(securityName)){
			sb.append(" and u.\"name\" like :name ");
		}
		if(!StringUtils.isNull(state)){
			sb.append(" and u.disabled = :state ");
		}
		if(ListUntils.isNotNull(manageIds)){
			sb.append(" and cur.manage_id in :manageIds ");
		}
		if(!StringUtils.isNull(isActivation)){
			sb.append(" and u.is_activation = :isActivation");
		}
		Query query = adminEntity.createNativeQuery(sb.toString());
		if(!StringUtils.isNull(securityName)){
			query.setParameter("name", "%"+securityName+"%");
		}
		if(!StringUtils.isNull(state)){
			boolean stateFlag = false;
			if("1".equals(state)){
				stateFlag = true;
			}
			if("0".equals(state)){
				stateFlag = false;
			}
			query.setParameter("state", stateFlag);
		}
		if(!StringUtils.isNull(isActivation)){
			boolean stateFlag = false;
			if("1".equals(isActivation)){
				stateFlag = true;
			}
			if("0".equals(isActivation)){
				stateFlag = false;
			}
			query.setParameter("isActivation", stateFlag);
		}
		if(ListUntils.isNotNull(manageIds)){
			query.setParameter("manageIds", manageIds);
		}
		query.setParameter("usermanageId", usermanageId);
		query.setParameter("clientId", clientId);
		return StringUtils.toLong(query.getSingleResult());	
	}

	/**
	 * 修改安全员状态
	 */
	@Transactional
	public Map<String, String> modifySecurityUserState(ModifySecurityUserStatusParam param,HttpServletRequest request) {
		User currentUser = UserUtils.getCurrentUser();
		Map<String, String> map = new HashMap<String,String>();
		List<String> ids = param.getSecurityUserIds();
		List<User> users = userRes.getUserBuIds(ids);
		String state = param.getState();
		Boolean s = null;
		if("0".equals(state)){
			s = false;
		}
		if("1".equals(state)){
			s = true;
		}
		userRes.modifyBusinessUsersStatus(ids, s);
		GrantLog grantLog = LogUtils.CreateGrantLog(request);
		threadPoolTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					grantLogApplication.addLog(currentUser,grantLog,LogConstants.MOD_SECURITYUSER_STATE,LogConstants.MOD_SECURITYUSER_STATE_NAME,"1","修改成功",param,users);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		});
		map.put("flag", "success");
		map.put("message", "修改成功!");
		return map;
	}

	/**
	 * 注销安全员
	 */
	public Map<String, String> cancellationSecurityUser(CancellationSecurityUserParam param,HttpServletRequest request) {
		User currentUser = UserUtils.getCurrentUser();
		Map<String, String> map = new HashMap<String,String>();
		List<String> ids = param.getSecurityUserIds();
		List<User> users = userRes.getUserBuIds(ids);
		userRes.cancellationBusinessUser(true,ids);
		//增加日志
		GrantLog grantLog = LogUtils.CreateGrantLog(request);
		threadPoolTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					grantLogApplication.addLog(currentUser,grantLog,LogConstants.CANCEL_SECURITYUSER,LogConstants.CANCEL_SECURITYUSER_NAME,"1","注销成功",param,users);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		});
		map.put("flag", "success");
		map.put("message", "注销成功!");
		return map;
	}

	/**
	 * 为安全员设置管理范围
	 */
	@Transactional
	public Map<String, String> setManageScope(List<SetSecurityUserManageScopeParam> param,HttpServletRequest request) {
//		User currentUser = UserUtils.getCurrentUser();
		String userId = null;
		String manageOrgId = null;
		String manageOrgName = null;
		String ouName = null;
		String clientId = null;
		List<UserClientRel> rels = null;
		UserClientRel rel = null;
		Map<String, String> map = new HashMap<String,String>();
		map = checkSetManageParam(param);
		if(map.containsKey("flag") && map.get("flag").equals("fail")){
			map.put("flag","fail");
			map.put("message","数据处理异常!");
			return map;
		}
		if(map.containsKey("flag") && map.get("flag").equals("check")){
			return map;
		}
		if(ListUntils.isNotNull(param)){
			rels = new ArrayList<>();
			for(SetSecurityUserManageScopeParam dto:param){
				userId = dto.getSecurityUserId();
				manageOrgId = dto.getManageOrgId();
				manageOrgName = dto.getManageOrgName();
				ouName = dto.getOuName();
				//需要检测授权是否合法
//				boolean checkFlag = commonApplication.checkSecurityUserOu(userId, ouName);
//				if(!checkFlag){
//					map.put("flag", "fail");
//					map.put("message", "所选管理范围和用户已有管理范围不在同一节点");
//					return map;
//				}
				clientId = dto.getClientId();
				rel = userClientRelResource.getRelByuIdClientId(userId,clientId);
				if(null == rel){
					rel = new UserClientRel();
					rel.setId(idGenerator.next());
				}
				rel.setClientId(clientId);
				rel.setManageId(manageOrgId);
				rel.setManageName(manageOrgName);
				rel.setUserId(userId);
				rels.add(rel);
			}
		}
		userClientRelResource.save(rels);
		//增加日志
		GrantLog grantLog = LogUtils.CreateGrantLog(request);
		threadPoolTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
//					grantLogApplication.addLog(currentUser,grantLog,LogConstants.SET_SECURITYUSER_MANAGE,LogConstants.SET_SECURITYUSER_MANAGE_NAME,"1","设置成功",users,param);
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
			}
		});
		map.put("flag", "success");
		map.put("message", "分配成功!");
		return map;
	}

	private Map<String,String> checkSetManageParam(List<SetSecurityUserManageScopeParam> param) {
		String ouName = null;
		String clientId = null;
		String manageOrgCode = null;
		String manageOrgId = null;
		String manageOrgName = null;
		String userId = null;
		Map<String,String> map  = new HashMap<>();
		for(SetSecurityUserManageScopeParam p:param){
			if(simpleDataService.checkOrgInWhole(p.getManageOrgId())){
				map.put("flag", "check");
				map.put("message",p.getManageOrgName()+"正在进行组织关系整建制转接");
				return map;
			}
			ouName = p.getOuName();
			clientId = p.getClientId();
			manageOrgCode = p.getManageOrgCode();
			manageOrgId = p.getManageOrgId();
			manageOrgName = p.getManageOrgName();
			userId = p.getSecurityUserId();
			if(StringUtils.isNull(ouName) || StringUtils.isNull(clientId) || StringUtils.isNull(manageOrgCode) ||
					StringUtils.isNull(manageOrgId) || StringUtils.isNull(manageOrgName)){
				map.put("flag","fail");
				return map;
			}
		}
		map.put("flag","success");
		return map;
	}

	/**
	 * 导出授权码 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public String exportAuthorizationCode(ExportAuthorizationCodeParam param,HttpServletRequest request) throws FileNotFoundException, IOException {
		User currentUser = UserUtils.getCurrentUser();
		List<String> ids = param.getIds();
		List<User> users = userRes.getUserBuIds(ids);
		ExportCompareExcel detail = new ExportCompareExcel("",ExportAuthorizationCodeVo.class,1,StringUtils.toString(ids.size()));
		List<ExportAuthorizationCodeVo> voList = getExportAuthorizationCode(ids);
//		excel名字生成
		String fileName = "安全员授权码_"+DateUtils.getDate("yyyyMMddHHmmss")+"_"+StringUtils.getRandomNum(3)+".xls";
		String tempPath = configApp.queryConfigInfoByCode(ConfigEnum.TEMP_EXPORT_PATH.getCode()).getConfigValue();
		ExportCompareExcel excel = new ExportCompareExcel("", ExportAuthorizationCodeVo.class, 1).setDataList(voList);
		excel.writeFile(tempPath + fileName);
		InputStream stream = new FileInputStream(new File(
				tempPath, fileName));
		FtpUtil ftputil = new FtpUtil();
		FTPClient ftp = ftputil.getClient(configApp.queryConfigInfoByCode(ConfigEnum.SERVER.getCode()).getConfigValue(),
//				StringUtils.toInteger(configApp.queryConfigInfoByCode(ConfigEnum.PORT.getCode()).getConfigValue()),
				configApp.queryConfigInfoByCode(ConfigEnum.SERVER_ACCOUNT.getCode()).getConfigValue(),
				configApp.queryConfigInfoByCode(ConfigEnum.SERVER_PWD.getCode()).getConfigValue());
		FtpUtil.upload(ftp, configApp.queryConfigInfoByCode(ConfigEnum.EXPORT_PATH.getCode()).getConfigValue() + fileName, stream);
		FileUtil.deleteDir(new File(tempPath + fileName));
		//增加日志
//		GrantLog grantLog = LogUtils.CreateGrantLog(request);
//		threadPoolTaskExecutor.execute(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					grantLogApplication.addLog(currentUser,grantLog,LogConstants.EXPORT_SECURITYUSER_CODE,LogConstants.EXPORT_SECURITYUSER_CODE_NAME,"1","导出成功",users,downUrl+fileName);
//				} catch (Exception e) {
//					log.error(e.getMessage());
//				}
//			}
//		});
		return downUrl + fileName;
	}

	/**
	 * 根据id获取用户授权码
	 * @param ids
	 * @return
	 */
	private List<ExportAuthorizationCodeVo> getExportAuthorizationCode(List<String> ids) {
		if(ListUntils.isNull(ids)){
			return null;
		}
		StringBuffer sb = new StringBuffer("SELECT\n" +
				"	u.\"id\",\n" +
				"	u.id_card,\n" +
				"	u.\"name\",\n" +
				"	u.authorization_code\n" +
				"FROM\n" +
				"	t_4a_actors u\n" +
				"WHERE\n" +
				"	u.\"id\" IN :ids");
		Query q = adminEntity.createNativeQuery(sb.toString());
		q.setParameter("ids", ids);
		List<Object[]> objectList = q.getResultList();
		List<ExportAuthorizationCodeVo> dtoList = null;
		ExportAuthorizationCodeVo vo = null;
		if(ListUntils.isNotNull(objectList)){
			dtoList = new ArrayList<ExportAuthorizationCodeVo>();
			for(Object[] o:objectList){
				if(null != o){
					vo = new ExportAuthorizationCodeVo();
					vo.setAuthorizationCode(StringUtils.toString(o[3]));
					vo.setIdCard(StringUtils.toString(o[1]));
					vo.setRelName(StringUtils.toString(o[2]));
//					vo.setUserId(StringUtils.toString(o[0]));
					dtoList.add(vo);
				}
			}
		}
		return dtoList;
	}

	@Override
	@Transactional
	public boolean cancelSecUserClient(CancelSecUserClientParam param) {
		List<String> ids = param.getIds();
		List<String> only1List = new ArrayList<>();
		List<String> otherList = new ArrayList<>();
		String clientId = param.getClientId();
		//查询所有安全员的user_client_rel
		List<UserClientRel> rels = userClientRelResource.getClientRelByUserIds(ids,clientId);
		if(ListUntils.isNotNull(rels)){
			//先查出这些id中所有用户与应用关联只有1个的
			StringBuffer sb = new StringBuffer("select l.user_id,count(DISTINCT l.id) " +
					"from r_4a_user_client l where l.user_id in :id GROUP BY l.user_id");
			Query q = adminEntity.createNativeQuery(sb.toString());
			q.setParameter("id",ids);
			List<Object[]> oList = q.getResultList();
			if(ListUntils.isNotNull(oList)){
				for(Object[] o:oList){
					if(StringUtils.toInteger(o[1])==1){
						only1List.add(StringUtils.toString(o[0]));
					}else{
						otherList.add(StringUtils.toString(o[0]));
					}
				}
			}
		}
		List<User> users = null;
		List<UserVpnRel> vpns = null;
		//如果用户没有其他的应用则 删除账号
		if(ListUntils.isNotNull(only1List)){
			users = userRes.getUserBuIds(only1List);
			vpns = userVpnRelResources.getRelByUserIds(only1List);
			//删除用户  vpn 以及用户vpn关联
			userRes.delete(users);
			userClientRelResource.delete(rels);
			ldapApplication.deleteBatch(vpns);
			userVpnRelResources.delete(vpns);
		}
		if(ListUntils.isNotNull(otherList)){
			userClientRelResource.delete(rels);
		}
		return true;
	}

	/**
	 * 检测撤销用户是否存在会删除账号的情况
	 */
	public List<String> checkCancelSecUser(CancelSecUserClientParam param) {
		List<String> only1List = new ArrayList<>();
		String clientId = param.getClientId();
		List<String> ids = param.getIds();
		//先查出这些id中所有用户与应用关联只有1个的
		StringBuffer sb = new StringBuffer("select l.user_id,count(DISTINCT l.id) " +
				"from r_4a_user_client l where l.user_id in :id GROUP BY l.user_id");
		Query q = adminEntity.createNativeQuery(sb.toString());
		q.setParameter("id",ids);
		List<Object[]> oList = q.getResultList();
		if(ListUntils.isNotNull(oList)){
			for(Object[] o:oList){
				if(StringUtils.toInteger(o[1])==1){
					only1List.add(StringUtils.toString(o[0]));
				}
			}
		}
		return only1List;
	}


	/**
	 * 整建制删除安全员所有账号信息
	 * @return
	 */
	@Transactional
	public boolean delSecUserClient(List<String> ids){
		List<String> otherList = new ArrayList<>();
		//查询所有安全员的user_client_rel
		List<UserClientRel> rels = userClientRelResource.getAllUserInfoRelByUserIds(ids);
		List<User> users = null;
		List<UserVpnRel> vpns = null;
		if(ListUntils.isNotNull(rels)){
			users = userRes.getUserBuIds(ids);
			vpns = userVpnRelResources.getRelByUserIds(ids);
			//删除用户  vpn 以及用户vpn关联
			userRes.delete(users);
			userClientRelResource.delete(rels);
			ldapApplication.deleteBatch(vpns);
			userVpnRelResources.delete(vpns);

			if(null!=users) {
				DelSecurityUserLog log = new DelSecurityUserLog();
				log.setId(StringUtils.toString(idGenerator.next()));
				log.setCreateDate(new Date());
				log.setIdCard(users.get(0).getIdCard());
				log.setOrgCode(rels.get(0).getManageCode());
				log.setOrgName(rels.get(0).getManageName());
				log.setOrgId(rels.get(0).getManageId());
				log.setOu(vpns.get(0).getOuName());
				log.setRelName(users.get(0).getName());
				delSecurityUserResource.save(log);
			}
		}
		return true;


	}

	@Override
	public List<UserClientRel> getRelByManageId(String orgId) {
		return userClientRelResource.getRelByManageId(orgId);
	}

	/**
	 * 党组织改名操作
	 * @param newOrgName
	 * @param orgId
	 * @return
	 */
	public int reNameOrgName(String newOrgName, String orgId){

		if(StringUtils.isNull(newOrgName)){
			return 0;
		}
		if(StringUtils.isNull(orgId)){
			return 0;
		}
		log.info("安全中心改名========>+SecurityUserApplicationImpl==========+reNameOrgName");
		return userClientRelResource.updateOrgNameByOrgId(newOrgName,orgId);
	}


	/**
	 * 17地市县级辅助安全员权限模块改名
	 * @param newOrgName
	 * @param orgId
	 * @return
	 */
	public int reNameAssistOrgName(String newOrgName, String orgId){

		if(StringUtils.isNull(newOrgName)){
			return 0;
		}
		if(StringUtils.isNull(orgId)){
			return 0;
		}
		log.info("安全中心辅助安全员权限模块改名========>+SecurityUserApplicationImpl========+reNameAssistOrgName");
		return assistCountyResources.updateOrgNameByOrgId(newOrgName,orgId);
	}
}
