package com.ido85.party.aaaa.mgmt.business.application.impl;

import com.ido85.party.aaaa.mgmt.business.application.BusinessUserApplication;
import com.ido85.party.aaaa.mgmt.business.application.ClientApplication;
import com.ido85.party.aaaa.mgmt.business.application.RuleApplication;
import com.ido85.party.aaaa.mgmt.business.domain.*;
import com.ido85.party.aaaa.mgmt.business.dto.*;
import com.ido85.party.aaaa.mgmt.business.resources.*;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.BusinessUserRoleDto;
import com.ido85.party.aaaa.mgmt.dto.BusinessUserRoleQueryParam;
import com.ido85.party.aaaa.mgmt.dto.CheckRoleParam;
import com.ido85.party.aaaa.mgmt.dto.base.ConfigEnum;
import com.ido85.party.aaaa.mgmt.ldap.application.LdapApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.CommonApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.ConfigApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.GrantLogApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.UserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.GrantLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserClientRelResource;
import com.ido85.party.aaaa.mgmt.security.common.LogUtils;
import com.ido85.party.aaaa.mgmt.security.common.UserUtils;
import com.ido85.party.aaaa.mgmt.security.constants.LogConstants;
import com.ido85.party.aaaa.mgmt.security.utils.*;
import com.ido85.party.aaaa.mgmt.security.utils.excel.ExportCompareExcel;
import com.ido85.party.aaaa.mgmt.service.LogService;
import com.ido85.party.aaaa.mgmt.service.RealnameAuthenticaitonClient;
import com.ido85.party.aaaa.mgmt.service.SimpleDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.*;
import java.util.*;


@Named
@Slf4j
public class BusinessUserApplicationImpl implements BusinessUserApplication {

	@PersistenceContext(unitName = "business")
	private EntityManager businessEntity;

	@Inject
	private BusinessUserResources businessUserResource;

	@Inject
	private ClientUserRelResources clientUserRelResources;

	@Value("${queryUserInfoByHashUrl}")
	private String queryUserInfoByHashUrl;

	@Inject
	private IdGenerator idGenerator;

	@Inject
	private BusinessRoleResources businessRoleResources;

	@Inject
	private LogService logService;

	@Inject
	private ConfigApplication configApp;

	@Value("${downUrl}")
	private String downUrl;

	@Inject
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@Inject
	private GrantLogApplication grantLogApplication;

	@Inject
	private BusinessUserResources businessUserResources;

	@Inject
	private CommonApplication commonApplication;

	@Inject
	private ClientApplication clientApplication;

	@Inject
	private RestTemplate restTemplate;

	@Inject
	private CLientExpandResources cLientExpandResources;

	@Inject
	private RealnameAuthenticaitonClient realnameAuthenticaitonClient;

	@Inject
	private UserApplication userApplication;

	@Inject
	private BusinessuserVpnRelResources businessuserVpnRelResources;

	@Inject
	private LdapApplication ldapApplication;

	@Inject
	private UserRoleResource userRoleResource;

	@Inject
	private UserClientRelResource userClientRelResource;

	@Value("${checkUserExistUrl}")
	private String checkUserExistUrl;//调用简向库地址

	@Value("${config.isneedauth}")
	private String isneedAuth;
	@Inject
	private SimpleDataService simpleDataService;

	@Inject
	private RuleApplication ruleApplication;

	/**
	 * 查询下级业务管理员
	 * 1.根据clientid查询分级管理的地址
	 * 2.根据地址获取所有下级id
	 * 3.查询
	 */
	public List<BusinessUserDto> getBusinessUserByCondition(@Valid BusinessUserQueryParam param) {
		//参数
		String businessUserName = param.getBusinessUserName();
		String clientId = param.getClientId();
		String state = param.getState();
		Integer pageNo = param.getPageNo();
		Integer pageSize = param.getPageSize();
		String manageId = param.getManageOrgId();
		String level = param.getLevel();//1省  2市  3县
		String isActivation = param.getIsActivation();
		List<BusinessUserDto> dtoList = null;
		User user = null;
		String usermanageId = null;
		user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		usermanageId = userClientRelResource.getManageIdByUserIdClientId(user.getId(), clientId);
		if (StringUtils.isNull(usermanageId)) {
			return null;
		}
		//根据clientid查询分级管理的地址,根据地址获取所有下级id
		List<String> manageIds = new ArrayList<>();
		/**
		 * 如果level为1或者2，则只查询当前级别
		 * 如果level为3，则查询下面所有
		 */
		if ("3".equals(level) || "4".equals(level)) {
			manageIds = clientApplication.getChildManageIds(clientId, manageId);
			manageIds.add(manageId);
		} else if (StringUtils.isNull(level) && StringUtils.isNull(manageId)) {
			manageIds.add(usermanageId);
		} else if ("1".equals(level) || "2".equals(level)) {
			manageIds.add(manageId);
		} else if (StringUtils.isNull(level) && !StringUtils.isNull(manageId)) {
			manageIds = clientApplication.getChildManageIds(clientId, manageId);
			manageIds.add(manageId);
		}
//		if(!StringUtils.isNull(manageId)){
//			manageIds = clientApplication.getChildManageIds(clientId,manageId);
//			manageIds.add(usermanageId);
//		}else{
//			//查询该用户该应用的管理范围
//			manageIds = clientApplication.getChildManageIds(clientId, usermanageId);
//			manageIds.add(usermanageId);
//		}
		StringBuffer sb = new StringBuffer("SELECT distinct\n" +
				"	u.\"id\",\n" +
				"	u.\"name\",\n" +
				"	u.authorization_code,\n" +
				"	u.id_card,\n" +
				"	u.is_account_locked,\n" +
				"	u.tele_phone,\n" +
				"	u.create_date,\n" +
				"   u.is_activation\n" +
				"FROM\n" +
				"	t_4a_actors u\n");
		if (ListUntils.isNotNull(manageIds)) {
			sb.append("LEFT JOIN r_4a_user_role ur ON u.\"id\" = ur.user_id\n");
//							"LEFT JOIN t_4a_role cr on cr.id=ur.role_id\n");
		}
		sb.append(
				"left join r_4a_client_user_rel cur on u.id=cur.user_id WHERE\n" +
						"	u.is_account_expired = 'f' and cur.client_id = :clientId");
//				if(ListUntils.isNotNull(manageIds)){
//					sb.append(" and cr.client_id = :clientId ");
//				}
		if (!StringUtils.isNull(businessUserName)) {
			sb.append(" and u.\"name\" like :name ");
		}
		if (!StringUtils.isNull(state)) {
			sb.append(" and u.disabled = :state ");
		}
		if (ListUntils.isNotNull(manageIds)) {
			sb.append(" and ur.manage_id in :manageIds ");
		}
		if (!StringUtils.isNull(isActivation)) {
			sb.append(" and u.is_activation = :isActivation ");
		}
		sb.append(" order by u.create_date desc");
		Query query = businessEntity.createNativeQuery(sb.toString());
		if (!StringUtils.isNull(businessUserName)) {
			query.setParameter("name", "%" + businessUserName + "%");
		}
		if (!StringUtils.isNull(state)) {
			boolean stateFlag = false;
			if ("1".equals(state)) {
				stateFlag = true;
			}
			if ("0".equals(state)) {
				stateFlag = false;
			}
			query.setParameter("state", stateFlag);
		}
		if (!StringUtils.isNull(isActivation)) {
			boolean stateFlag = false;
			if ("1".equals(isActivation)) {
				stateFlag = true;
			}
			if ("0".equals(isActivation)) {
				stateFlag = false;
			}
			query.setParameter("isActivation", stateFlag);
		}
		if (ListUntils.isNotNull(manageIds)) {
			query.setParameter("manageIds", manageIds);
		}
		query.setParameter("clientId", clientId);
		query.setFirstResult((pageNo - 1) * pageSize);
		query.setMaxResults(pageSize);
		List<Object[]> objectList = query.getResultList();
		if (null != objectList && objectList.size() > 0) {
			dtoList = new ArrayList<BusinessUserDto>();
//			userIds = new ArrayList<String>();
			BusinessUserDto dto = null;
			for (Object[] object : objectList) {
				dto = new BusinessUserDto();
				dto.setAuthorizationCode(StringUtils.toString(object[2]));
				dto.setBusinessUserId(StringUtils.toString(object[0]));
				dto.setBusinessUserName(StringUtils.toString(object[1]));
				dto.setIdCard(StringUtils.toString(object[3]));
				dto.setState(StringUtils.toString(object[4]).equals("false") ? "0" : "1");
				dto.setTelephone(StringUtils.toString(object[5]));
				dto.setCreateDate(StringUtils.toString(object[6]));
				dto.setIsActivation(StringUtils.toString(object[7]).equals("false") ? "0" : "1");
				dtoList.add(dto);
//				userIds.add(StringUtils.toString(object[0]));
			}
		}
		return dtoList;
	}


	/**
	 * 业务管理员总数查询
	 */
	public Long getBusinessUserCntByCondition(BusinessUserQueryParam param) {
		//参数
		String businessUserName = param.getBusinessUserName();
		String clientId = param.getClientId();
		String state = param.getState();
		String manageId = param.getManageOrgId();
		List<BusinessUserDto> dtoList = null;
		String level = param.getLevel();
		User user = null;
		String usermanageId = null;
		String isActivation = param.getIsActivation();
		user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		usermanageId = userClientRelResource.getManageIdByUserIdClientId(user.getId(), clientId);
		if (StringUtils.isNull(usermanageId)) {
			return null;
		}
		//根据clientid查询分级管理的地址,根据地址获取所有下级id
		List<String> manageIds = new ArrayList<>();
		/**
		 * 如果level为1或者2，则只查询当前级别
		 * 如果level为3，则查询下面所有
		 */
		if ("3".equals(level) || "4".equals(level)) {
			manageIds = clientApplication.getChildManageIds(clientId, manageId);
			manageIds.add(manageId);
		} else if (StringUtils.isNull(level) && StringUtils.isNull(manageId)) {
			manageIds.add(usermanageId);
		} else if ("1".equals(level) || "2".equals(level)) {
			manageIds.add(manageId);
		} else if (StringUtils.isNull(level) && !StringUtils.isNull(manageId)) {
			manageIds = clientApplication.getChildManageIds(clientId, manageId);
			manageIds.add(manageId);
		}
//		//如果查询条件管理范围不为空，则查询查询范围下一级的人。如果为空，则查询此用户管理范围下的人
//		if(!StringUtils.isNull(manageId)){
//			manageIds = clientApplication.getChildManageIds(clientId,manageId);
//			manageIds.add(usermanageId);
//		}else{
//			//查询该用户该应用的管理范围
//			manageIds = clientApplication.getChildManageIds(clientId, usermanageId);
//			manageIds.add(usermanageId);
//		}
		StringBuffer sb = new StringBuffer("SELECT \n" +
				"	count(distinct u.\"id\")\n" +
				"FROM\n" +
				"	t_4a_actors u\n");
		if (ListUntils.isNotNull(manageIds)) {
			sb.append("LEFT JOIN r_4a_user_role ur ON u.\"id\" = ur.user_id\n");
//									"LEFT JOIN t_4a_role cr on cr.id=ur.role_id\n");
		}
		sb.append(
				"left join r_4a_client_user_rel cur on u.id=cur.user_id WHERE\n" +
						"	u.is_account_expired = 'f' and cur.client_id = :clientId");
//						if(ListUntils.isNotNull(manageIds)){
//							sb.append(" and cr.client_id = :clientId ");
//						}
		if (!StringUtils.isNull(businessUserName)) {
			sb.append(" and u.\"name\" like :name ");
		}
		if (!StringUtils.isNull(state)) {
			sb.append(" and u.disabled = :state ");
		}
		if (ListUntils.isNotNull(manageIds)) {
			sb.append(" and ur.manage_id in :manageIds ");
		}
		if (!StringUtils.isNull(isActivation)) {
			sb.append(" and u.is_activation = :isActivation ");
		}
		Query query = businessEntity.createNativeQuery(sb.toString());
		if (!StringUtils.isNull(businessUserName)) {
			query.setParameter("name", "%" + businessUserName + "%");
		}
		if (!StringUtils.isNull(state)) {
			boolean stateFlag = false;
			if ("1".equals(state)) {
				stateFlag = true;
			}
			if ("0".equals(state)) {
				stateFlag = false;
			}
			query.setParameter("state", stateFlag);
		}
		if (!StringUtils.isNull(isActivation)) {
			boolean stateFlag = false;
			if ("1".equals(isActivation)) {
				stateFlag = true;
			}
			if ("0".equals(isActivation)) {
				stateFlag = false;
			}
			query.setParameter("isActivation", stateFlag);
		}
		if (ListUntils.isNotNull(manageIds)) {
			query.setParameter("manageIds", manageIds);
		}
		query.setParameter("clientId", clientId);
		return StringUtils.toLong(query.getSingleResult());
	}


	@Override
	public BusinessUser getBusinessUserById(String userId) {
		return businessUserResource.getOne(userId);
	}

	@Override
	public void saveBusinessUser(BusinessUser user) {
		businessUserResource.save(user);
	}

	/**
	 * 开通业务员
	 * TODO 需要优化
	 */
	public Map<String, String> addBusinessUser(@Valid List<AddBusinessUser> param, HttpServletRequest request) {
		Map<String, String> result = new HashMap<String, String>();
		//检验参数
		result = CheckParam(param);
		if (result.containsKey("flag") && result.get("flag").equals("fail")) {
			result.put("message", "数据处理异常!");
			return result;
		}
		if(result.containsKey("flag") && result.get("flag").equals("check")){
			return result;
		}
		User createOr = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (ListUntils.isNotNull(param)) {
			for (AddBusinessUser addbusinessUser : param) {
				if (StringUtils.isNull(addbusinessUser.getIdCard())) {
					result.put("flag", "fail");
					result.put("message", "请填写身份证号!");
					return result;
				}
				if (StringUtils.isNull(addbusinessUser.getRelName())) {
					result.put("flag", "fail");
					result.put("message", "请填写真实姓名!");
					return result;
				}
				if (!StringUtils.isTelephone(addbusinessUser.getTelephone())) {
					result.put("flag", "fail");
					result.put("message", "请填写正确的手机号!");
					return result;
				}
				log.info("-------------idCard:" + addbusinessUser.getIdCard());
				String hash = StringUtils.getUserNameIDHash(addbusinessUser.getRelName(), addbusinessUser.getIdCard());
				log.info("-------------已经生成hash:" + hash);
				//当创建业务管理员时，不能为审计员或者安全员
				boolean isSecOrAu = commonApplication.checkIsSecOrAudi(hash);
				if (isSecOrAu) {
					result.put("flag", "fail");
					result.put("message", "用户已经为安全员或者审计员!");
					return result;
				}
				BusinessUser u = businessUserResource.getUserByHash(hash);
				if (null != u) {
					return addBusinessUserClientRel(addbusinessUser,u);
				} else {
					return addBusinessUser(addbusinessUser,hash,createOr);
				}
			}
		}
		return result;
	}

	private Map<String,String> addBusinessUser(AddBusinessUser addbusinessUser,String hash,User createOr) {
		BusinessUser user = new BusinessUser();
		boolean checkResult = false;
		StringBuffer idCards = new StringBuffer();
		Map<String, String> result = new HashMap<String, String>();
		List<BusinessUser> entities = new ArrayList<BusinessUser>();
		StringBuffer message = new StringBuffer();
		ClientUserRel cur = null;
		if("1".equals(isneedAuth)){//如果是1  则需要党员认证  为0则不需要
			//将原来的公安部实名认证修改为实名认证备案的党员
			checkResult = CheckUserExistFromSimple(hash);
			if (!checkResult) {
				result.put("flag", "fail");
				result.put("message", "管理员必须为实名认证备案的党员");
				return result;
			}
		}

		String code = StringUtils.getRandomNum(8);
		String userId = StringUtils.toString(idGenerator.next());
		user.setId(userId);
		user.setAccountExpired(false);
		user.setAccountLocked(false);
		user.setPwdExpired(false);
		user.setActivation(false);
		user.setAuthorizationCode(code);
		user.setCreateDate(new Date());
		user.setDisabled(false);
		user.setEnabled(true);
		user.setTelePhone(addbusinessUser.getTelephone());
		user.setHash(hash);
		user.setIdCard(addbusinessUser.getIdCard());
		user.setName(addbusinessUser.getRelName());
		if(null != createOr){
			user.setCreateBy(createOr.getId());
		}
		entities.add(user);
		idCards.append(addbusinessUser.getIdCard()+" ");
		String clientId = addbusinessUser.getClientId();
		cur = new ClientUserRel();
		cur.setClientId(clientId);
		cur.setClientUserRelId(idGenerator.next());
		cur.setUserId(userId);
		//授权一个应用,应该检测他在该应用下的多个管理范围所在节点是否一致
		List<RoleDto> grp =  addbusinessUser.getRoles();
		result = grantRoleParam(grp,userId,clientId,addbusinessUser.getIdCard(),"0",cur,entities,code,addbusinessUser.getTelephone());
		if(result.containsKey("flag") && result.get("flag").equals("fail")){
			return result;
		}
//		else{
//			String ou = result.get("message");
//			boolean vpnFlag = commonApplication.createVPN(userId,code,addbusinessUser.getTelephone(),addbusinessUser.getIdCard(),ou);
//		}
		message.append(userId).append("|").append(addbusinessUser.getRelName()).append(",");
		result.put("flag", "success");
		result.put("message",message.toString().substring(0, message.length() - 1));
		return result;
	}

	private Map<String, String> addBusinessUserClientRel(AddBusinessUser addbusinessUser,BusinessUser u) {
		Map<String, String> result = new HashMap<String, String>();
		StringBuffer message = new StringBuffer();
		ClientUserRel cur = null;
		String clientId = addbusinessUser.getClientId();
		//判断用户是否在该应用下已经存在
		List<ClientUserRel> clientUserRels = clientUserRelResources.getCurByClientUser(clientId, u.getId());
		if (ListUntils.isNotNull(clientUserRels)) {
			result.put("flag", "fail");
			result.put("message", "该用户已经有当前应用的管理权限");
			return result;
		}
		cur = new ClientUserRel();
		cur.setClientId(clientId);
		cur.setClientUserRelId(idGenerator.next());
		cur.setUserId(u.getId());
		//授权一个应用
		List<RoleDto> grp = addbusinessUser.getRoles();
		result = grantRoleParam(grp, u.getId(), clientId, u.getIdCard(), "1",cur,null,null,null);
		if (result.containsKey("flag") && result.get("flag").equals("fail")) {
			return result;
		}
//		clientUserRelResources.save(cur);
		message.append(u.getId()).append("|").append(u.getName()).append(",");
		result.put("flag", "success");
		result.put("message",message.toString().substring(0, message.length() - 1));
		return result;
	}

	/**
	 * 校验参数
	 *
	 * @param param
	 * @return
	 */
	private Map<String, String> CheckParam(List<AddBusinessUser> param) {
		Map<String, String> result = new HashMap<>();
		String clientId = null;
		List<RoleDto> roles = null;
		String manageId = null;
		String manageCode = null;
		String manageName = null;
		String ou = null;
		Long roleId = null;
		for (AddBusinessUser u : param) {
			clientId = u.getClientId();
			if (StringUtils.isNull(clientId)) {
				result.put("flag", "fail");
				return result;
			}
			roles = u.getRoles();
			if(ListUntils.isNull(roles)){
				result.put("flag", "fail");
				return result;
			}
			for (RoleDto dto : roles) {
				manageId = dto.getManageId();
				manageCode = dto.getManageCode();
				manageName = dto.getManageName();
				ou = dto.getOuName();
				roleId = dto.getRoleId();
				if (StringUtils.isNull(manageId)) {
					result.put("flag", "fail");
					return result;
				}
				if(simpleDataService.checkOrgInWhole(manageId)){
					result.put("flag", "check");
					result.put("message",manageName+"正在进行组织关系整建制转接");
					return result;
				}
				if (StringUtils.isNull(manageCode)) {
					result.put("flag", "fail");
					return result;
				}
				if (StringUtils.isNull(manageName)) {
					result.put("flag", "fail");
					return result;
				}
				if(StringUtils.isNull(ou)){
					result.put("flag", "fail");
					return result;
				}
				if (null == roleId) {
					result.put("flag", "fail");
					return result;
				}
			}
		}
		result.put("flag", "success");
		return result;
	}

	@Transactional
	private Map<String, String> grantRoleParam(List<RoleDto> grp, String userId, String clientId, String idCard, String type,ClientUserRel cur,List<BusinessUser> entities,String code,String telephone) {
		String ou = null;
		Set<String> ouList = new HashSet<>();
		BusinessUserVpnRel rel = null;
		UserRole ur = null;
		Map<String, String> result = new HashMap<String, String>();
		if (ListUntils.isNotNull(grp)) {
			List<UserRole> userRoleList = new ArrayList<>();
			List<UserRole> olduserRoleList = getUserRoleByClientUser(clientId, userId);
			for (RoleDto roledto : grp) {
				Long relId = roledto.getRelId();
				Long roleId = roledto.getRoleId();
				String manageId = roledto.getManageId();
				String manageName = roledto.getManageName();
				String manageCode = roledto.getManageCode();
				String manageType = roledto.getType();
				String manageLevel = roledto.getLevel();
//				manageType = "1";
//				manageLevel = "1";
				ou = roledto.getOuName();
				ouList.add(ou);
				//授权规则判断
				Map<String,String> map = ruleApplication.checkPermissionRule(roleId,manageType,manageLevel,manageId,manageCode);
				if(null != map && map.containsKey("flag") && "fail".equals(map.get("flag"))){
					return map;
				}
				ur = new UserRole();
				ur.setId(idGenerator.next());
				ur.setManageId(manageId);
				ur.setManageName(manageName);
				ur.setManageCode(manageCode);
				ur.setRoleId(roleId);
				ur.setUserId(userId);
				userRoleList.add(ur);
			}
			if (ouList.size() > 1) {
				result.put("flag", "fail");
				result.put("message", "您选择的所有管理范围中存在不属于同一节点的范围，请重新选择!");
				return result;
			}
			if(StringUtils.isNull(ou)){
				result.put("flag", "fail");
				result.put("message", "数据异常，请重新选择!");
				return result;
			}
			if ("1".equals(type)) {
				rel = businessuserVpnRelResources.getRelByUserId(userId);
				if (null != rel) {
					String ouName = rel.getOu();
					//如果原来账号在node0共享节点，新的账号在节点应用，则将vpn账号移动到节点区
					if ("node0".equals(ouName) && !"node0".equals(ou)) {
						ldapApplication.changeVPNOuPwd(ouName, rel.getVpn(), ou, userId);
					} else if (!"node0".equals(ou) && !ouName.equals(ou)) {
						result.put("flag", "fail");
						result.put("message", "您选择的所有管理范围与用户原有管理范围不在同一节点，请重新选择!");
						return result;
					}
				}
			}
			if("0".equals(type)){
				boolean vpnFlag = commonApplication.createVPN(userId,code,telephone,idCard,ou);
			}
			businessUserResource.save(entities);
			userRoleResource.delete(olduserRoleList);
			userRoleResource.save(userRoleList);
			clientUserRelResources.save(cur);
		}
		result.put("flag", "success");
		result.put("message", ou);
		return result;
	}


	/**
	 * 状态修改
	 */
	@Transactional
	public Map<String, String> modifyBusinessUserStatus(ModifyBusinessUserStatusParam param, HttpServletRequest request) {
		User currentUser = UserUtils.getCurrentUser();
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer idCards = new StringBuffer();
		List<String> ids = param.getBusinessUserIds();
		List<BusinessUser> users = businessUserResources.getUserByIds(ids);
		for (String id : param.getBusinessUserIds()) {
			idCards.append(id + " ");
		}
		String state = param.getState();
		Boolean s = null;
		if ("0".equals(state)) {
			s = false;
		}
		if ("1".equals(state)) {
			s = true;
		}
		businessUserResource.modifyBusinessUsersStatus(ids, s);

		//增加日志
		GrantLog grantLog = LogUtils.CreateGrantLog(request);
		threadPoolTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					grantLogApplication.addLog(currentUser, grantLog, LogConstants.MOD_BUSINESSUSER_STATE, LogConstants.MOD_SECURITYUSER_STATE_NAME, "1", "修改成功", param, users);
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
	 * 注销业务管理员
	 */
	@Transactional
	public Map<String, String> cancellationBusinessUser(CancellationBusinessUserParam param, HttpServletRequest request) {
		User currentUser = UserUtils.getCurrentUser();
		Map<String, String> map = new HashMap<String, String>();
		List<String> ids = param.getBusinessUserIds();
		List<BusinessUser> users = businessUserResources.getUserByIds(ids);
		businessUserResource.cancellationBusinessUser(true, ids);
		StringBuffer idCards = new StringBuffer();
		for (String id : param.getBusinessUserIds()) {
			idCards.append(id + " ");
		}
		//增加日志
		GrantLog grantLog = LogUtils.CreateGrantLog(request);
		threadPoolTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					grantLogApplication.addLog(currentUser, grantLog, LogConstants.CANCEL_BUSINESSUSER, LogConstants.CANCEL_BUSINESSUSER_NAME, "1", "注销成功", param, users);
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
	 * 为业务管理员设置管理范围（批量）
	 */
	@Transactional
	public Map<String, String> setManageScope(SetManageScopeParam param, HttpServletRequest request) {
		User currentUser = UserUtils.getCurrentUser();
		Map<String, String> map = new HashMap<String, String>();
		List<String> ids = param.getBusinessUserIds();
		List<BusinessUser> users = businessUserResources.getUserByIds(ids);
		String manageOrgId = param.getManageOrgId();
		String manageOrgName = param.getManageOrgName();
		String manageOrgCode = param.getManageOrgCode();
		if (null != param) {
//			businessUserResource.setManageScope(manageOrgId,manageOrgName,manageOrgCode,ids);
		}
		//增加日志
		GrantLog grantLog = LogUtils.CreateGrantLog(request);
		threadPoolTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					grantLogApplication.addLog(currentUser, grantLog, LogConstants.SET_BUSINESSUSER_MANAGE, LogConstants.SET_BUSINESSUSER_MANAGE_NAME, "1", "设置成功", users, param);
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

	/**
	 * 给业务管理员授权
	 */
	@Transactional()
//	@org.springframework.transaction.annotation.Transactional(transactionManager="transactionManagerBusiness")
	public Map<String, String> grantBusinessUser(List<GrantBusinessUserParam> param, HttpServletRequest request) {
		List<UserRole> userRoleList = new ArrayList<>();
		List<UserRole> olduserRoleList = new ArrayList<>();
		Map<String, String> map = new HashMap<String, String>();
		UserRole ur = null;
		map = checkGrantParam(param);
		if (map.containsKey("flag") && map.get("flag").equals("fail")) {
			map.put("message", "数据处理异常!");
			return map;
		}
		if(map.containsKey("flag") && map.get("flag").equals("check")){
			return map;
		}
//		Set<BusinessRole> userRoles = null;
		if (ListUntils.isNotNull(param)) {
			for (GrantBusinessUserParam grantBusinessUserParam : param) {
				String userId = grantBusinessUserParam.getBusinessUserId();
				List<GrantRoleParam> clientList = grantBusinessUserParam.getClients();
				for (GrantRoleParam client : clientList) {
					String clientId = client.getClientId();
					//获取该clientid该userid下的userrole
					olduserRoleList = getUserRoleByClientUser(clientId, userId);
					List<RoleDto> roleList = client.getRoles();
					for (RoleDto roledto : roleList) {
						Long roleId = roledto.getRoleId();
						String manageId = roledto.getManageId();
						String manageName = roledto.getManageName();
						String manageCode = roledto.getManageCode();
						String ouName = roledto.getOuName();
						String manageType = roledto.getType();
						String manageLevel = roledto.getLevel();
						//授权规则判断
						Map<String,String> judgeMap = ruleApplication.checkPermissionRule(roleId,manageType,manageLevel,manageId,manageCode);
						if(null != judgeMap && judgeMap.containsKey("flag") && "fail".equals(judgeMap.get("flag"))){
							return judgeMap;
						}
						//需要检测授权是否合法
						boolean checkFlag = commonApplication.checkBusinessOu(userId, ouName);
						if (!checkFlag) {
							map.put("flag", "fail");
							map.put("message", "所选管理范围和用户已有管理范围不在同一节点");
							return map;
						}
//						UserRole ur = userRoleResource.getRoleByUserIdRoleId(userId, roleId);
//						if (null != ur) {
//							ur.setManageId(manageId);
//							ur.setManageName(manageName);
//							ur.setManageCode(manageCode);
//							userRoleList.add(ur);
//						} else {
							ur = new UserRole();
							ur.setId(idGenerator.next());
							ur.setManageId(manageId);
							ur.setManageName(manageName);
							ur.setManageCode(manageCode);
							ur.setRoleId(roleId);
							ur.setUserId(userId);
							userRoleList.add(ur);
//						}
					}
				}
				userRoleResource.delete(olduserRoleList);
			}
			userRoleResource.save(userRoleList);
		}
		//增加日志
		GrantLog grantLog = LogUtils.CreateGrantLog(request);
		threadPoolTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
//					grantLogApplication.addLog(currentUser,grantLog,LogConstants.GRANT_BUSINESSUSER,LogConstants.GRANT_BUSINESSUSER_NAME,"1","授权成功",param,users);
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
			}
		});
		map.put("flag", "success");
		map.put("message", "授权成功!");
		return map;
	}

	private Map<String, String> checkGrantParam(List<GrantBusinessUserParam> param) {
		Map<String, String> map = new HashMap<>();
		String userId = null;
		List<GrantRoleParam> gp = null;
		List<RoleDto> rdList = null;
		String ouName = null;
		String manageName = null;
		String manageCode = null;
		String manageId = null;
		for (GrantBusinessUserParam p : param) {
			gp = p.getClients();
			for (GrantRoleParam grp : gp) {
				rdList = grp.getRoles();
				if(ListUntils.isNull(rdList)){
					map.put("flag", "fail");
					return map;
				}
				for (RoleDto rd : rdList) {
					ouName = rd.getOuName();
					manageName = rd.getManageName();
					manageCode = rd.getManageCode();
					manageId = rd.getManageId();
					if (StringUtils.isNull(ouName) || StringUtils.isNull(manageName) ||
							StringUtils.isNull(manageCode) || StringUtils.isNull(manageId)) {
						map.put("flag", "fail");
						return map;
					}
					if(simpleDataService.checkOrgInWhole(manageId)){
						map.put("flag", "check");
						map.put("message",manageName+"正在进行组织关系整建制转接");
						return map;
					}
				}
			}
		}
		map.put("flag", "success");
		return map;
	}

	private List<UserRole> getUserRoleByClientUser(String clientId, String userId) {
		StringBuffer sb = new StringBuffer("select ur from BusinessRole r,UserRole ur where r.id = ur.roleId");
		if (!StringUtils.isNull(clientId)) {
			sb.append(" and r.clientId = :clientId");
		}
		if (!StringUtils.isNull(userId)) {
			sb.append(" and ur.userId = :userId");
		}
		Query q = businessEntity.createQuery(sb.toString(), UserRole.class);
		if (!StringUtils.isNull(clientId)) {
			q.setParameter("clientId", clientId);
		}
		if (!StringUtils.isNull(userId)) {
			q.setParameter("userId", userId);
		}
		List<UserRole> userRoleList = q.getResultList();
		return userRoleList;
	}


	private boolean checkManageIdIsRight(String manageId, String manageId2) {
		// TODO Auto-generated method stub
		return true;
	}


//	private List<BusinessUser> getUsersfromParam(List<GrantBusinessUserParam> param) {
//		List<String> ids = new ArrayList<String>();
//		for(GrantBusinessUserParam p:param){
//			String id = p.getBusinessUserId();
//			ids.add(id);
//		}
//		List<BusinessUser> users = businessUserResources.getUserByIds(ids);
//		return users;
//	}

	/**
	 * 导出业务管理员授权码
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public String exportAuthorizationCode(ExportAuthorizationCodeParam param, HttpServletRequest request) throws FileNotFoundException, IOException {
		User currentUser = UserUtils.getCurrentUser();
		List<String> ids = param.getIds();
		List<BusinessUser> users = businessUserResources.getUserByIds(ids);
		ExportCompareExcel detail = new ExportCompareExcel("", ExportAuthorizationCodeVo.class, 1, StringUtils.toString(ids.size()));
		List<ExportAuthorizationCodeVo> voList = getExportAuthorizationCode(ids);
//		excel名字生成
		String fileName = "业务管理员授权码_" + DateUtils.getDate("yyyyMMddHHmmss") + "_" + StringUtils.getRandomNum(3) + ".xls";
		String tempPath = configApp.queryConfigInfoByCode(ConfigEnum.TEMP_EXPORT_PATH.getCode()).getConfigValue();
		ExportCompareExcel excel = new ExportCompareExcel("", ExportAuthorizationCodeVo.class, 1).setDataList(voList);
		excel.writeFile(tempPath + fileName);
		InputStream stream = new FileInputStream(new File(
				tempPath, fileName));
		FtpUtil ftputil = new FtpUtil();
		FTPClient ftp = ftputil.getClient(configApp.queryConfigInfoByCode(ConfigEnum.SERVER.getCode()).getConfigValue(),
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
//					grantLogApplication.addLog(currentUser,grantLog,LogConstants.EXPORT_BUSINESSUSER_CODE,LogConstants.EXPORT_BUSINESSUSER_CODE_NAME,"1","导出成功",users,downUrl+fileName);
//				} catch (Exception e) {
//					log.error(e.getMessage());
//				}
//			}
//		});
		return downUrl + fileName;
	}

	/**
	 * 根据id获取用户授权码
	 *
	 * @param ids
	 * @return
	 */
	private List<ExportAuthorizationCodeVo> getExportAuthorizationCode(List<String> ids) {
		if (ListUntils.isNull(ids)) {
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
		Query q = businessEntity.createNativeQuery(sb.toString());
		q.setParameter("ids", ids);
		List<Object[]> objectList = q.getResultList();
		List<ExportAuthorizationCodeVo> dtoList = null;
		ExportAuthorizationCodeVo vo = null;
		if (ListUntils.isNotNull(objectList)) {
			dtoList = new ArrayList<ExportAuthorizationCodeVo>();
			for (Object[] o : objectList) {
				if (null != o) {
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

	/**
	 * 根据用户id查询应用角色详情
	 */
	public List<ClientRolesDto> clientRoleQuery(String businessUserId, String cId) {
		if (StringUtils.isNull(businessUserId) || StringUtils.isNull(cId)) {
			return null;
		}
		List<ClientRolesDto> dtoList = null;
		ClientRolesDto clientRolesDto = null;
		List<RoleDto> roles = null;
		RoleDto roleDto = null;
		boolean flag = false;
		StringBuffer sb = new StringBuffer("SELECT\n" +
				"	ur.user_id,\n" +
				"	r.client_id,\n" +
				"	d.client_name,\n" +
				"	ur.role_id,\n" +
				"	r.\"name\",\n" +
				"	r.\"description\",\n" +
				"   ur.\"manage_id\",\n" +
				"   r.manage_name,\n" +
				"   r.is_common,\n" +
				"   ur.manage_name as user_managename,\n" +
				"   ur.id as rel_id\n" +
				"FROM\n" +
//				"	r_4a_client_role_rel crr,\n" +
				"	t_4a_role r,\n" +
				"	r_4a_user_role ur,\n" +
				"	client_expand d\n" +
				"WHERE\n" +
				"	ur.user_id = :id\n" +
				"AND r.client_id = d.client_id\n" +
				"AND r.id = ur.role_id\n" +
				"AND ur.role_id = r.\"id\" and r.client_id=:clientId ");
		Query q = businessEntity.createNativeQuery(sb.toString());
		q.setParameter("id", businessUserId);
		q.setParameter("clientId", cId);
		List<Object[]> oList = q.getResultList();
		if (ListUntils.isNotNull(oList)) {
			dtoList = new ArrayList<ClientRolesDto>();
			for (Object[] o : oList) {
				if (null != o) {
					String clientId = StringUtils.toString(o[1]);
					String clientName = StringUtils.toString(o[2]);
					roles = new ArrayList<RoleDto>();
					for (ClientRolesDto dto : dtoList) {
						if (null != dto && dto.getClientId().equals(StringUtils.toString(o[1]))) {
							clientRolesDto = dto;
							clientRolesDto.setClientId(clientId);
							clientRolesDto.setClientName(clientName);
							String dtoClientId = dto.getClientId();
							if (clientId.equals(dtoClientId)) {
								roleDto = new RoleDto();
								roleDto.setRoleId(StringUtils.toLong(o[3]));
								roleDto.setRoleName(StringUtils.toString(o[5]));
								roleDto.setManageId(StringUtils.toString(o[6]));
//								roleDto.setDescription(StringUtils.toString(o[5]));
								if (StringUtils.toString(o[8]).equals("true")) {
									roleDto.setManageName("共享角色");
								} else {
									roleDto.setManageName(StringUtils.toString(o[7]));
								}
								roleDto.setUserManageName(StringUtils.toString(o[9]));
								roleDto.setRelId(StringUtils.toLong(o[10]));
								roles.add(roleDto);
								roles.addAll(dto.getRoles());
								dto.setRoles(roles);
							}
							flag = true;
							break;
						}
					}
					if (!flag) {
						clientRolesDto = new ClientRolesDto();
						clientRolesDto.setClientId(clientId);
						clientRolesDto.setClientName(clientName);
						roleDto = new RoleDto();
						roleDto.setRoleId(StringUtils.toLong(o[3]));
						roleDto.setRoleName(StringUtils.toString(o[5]));
						roleDto.setManageId(StringUtils.toString(o[6]));
						if (StringUtils.toString(o[8]).equals("true")) {
							roleDto.setManageName("共享角色");
						} else {
							roleDto.setManageName(StringUtils.toString(o[7]));
						}
						roleDto.setUserManageName(StringUtils.toString(o[9]));
						roleDto.setRelId(StringUtils.toLong(o[10]));
						roles.add(roleDto);
						clientRolesDto.setRoles(roles);
						dtoList.add(clientRolesDto);
					}
					flag = false;
				}
			}
		}
		return dtoList;
	}


	/**
	 * 业务管理员角色查询
	 */
	public List<BusinessUserRoleDto> businessUserRoleQuery(BusinessUserRoleQueryParam param) {
		List<BusinessUserRoleDto> dtoList = null;
		BusinessUserRoleDto dto = null;
		String clientId = param.getClientId();
		String userId = param.getUserId();
		StringBuffer sb = new StringBuffer("select r.id,r.name,r.description,ur.manageId,r.manageName from UserRole ur,BusinessRole r "
				+ "where ur.roleId = r.id  and r.clientId=:clientId and ur.userId = :userId");
		Query q = businessEntity.createQuery(sb.toString());
		q.setParameter("clientId", clientId);
		q.setParameter("userId", userId);
		List<Object[]> oList = q.getResultList();
		if (ListUntils.isNotNull(oList)) {
			dtoList = new ArrayList<BusinessUserRoleDto>();
			for (Object[] o : oList) {
				dto = new BusinessUserRoleDto();
				dto.setDescription(StringUtils.toString(o[2]));
				dto.setManageId(StringUtils.toString(o[3]));
				dto.setManageName(StringUtils.toString(o[4]));
				dto.setRoleId(StringUtils.toString(o[0]));
				dto.setRoleName(StringUtils.toString(o[1]));
				dtoList.add(dto);
			}
		}

		return dtoList;
	}


	/**
	 * 检测角色授权是否合法
	 *
	 * @param param
	 * @return
	 */
	public boolean checkRole(CheckRoleParam param) {
//		String clientId = param.getClientId();
//		String roleManageId = param.getRoleManageId();
//		String type = param.getType();
//		User user = null;
//		String url = null;
//		user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		if(null != user){
//			//查询该用户该应用的管理范围
//			String manageId = clientUserRelResources.getManageIdByUserIdClientId(user.getId(),clientId);
//			if(type.equals("0") && manageId.equals(roleManageId)){
//				return true;
//			}
//			//查询该应用的url
//			ClientExpand ce = cLientExpandResources.getClientById(clientId);
//			if(null != ce){
//				url = ce.getCheckUrl()+"?firstId="+roleManageId+"&secondId="+manageId;
//			}
//			String flag = restTemplate.getForObject(url, String.class);
//			if(flag.equals("0")){
//				return true;
//			}
//		}
//		return false;
		return true;
	}

	@Override
	public List<String> checkcancelBusiUserClient(CancelBusiUserClientParam param) {
		List<String> only1List = new ArrayList<>();
		String clientId = param.getClientId();
		List<String> ids = param.getIds();
		//先查出这些id中所有用户与应用关联只有1个的
		StringBuffer sb = new StringBuffer("select l.user_id,count(DISTINCT l.client_user_rel_id) " +
				"from r_4a_client_user_rel l where l.user_id in :id GROUP BY l.user_id");
		Query q = businessEntity.createNativeQuery(sb.toString());
		q.setParameter("id", ids);
		List<Object[]> oList = q.getResultList();
		if (ListUntils.isNotNull(oList)) {
			for (Object[] o : oList) {
				if (StringUtils.toInteger(o[1]) == 1) {
					only1List.add(StringUtils.toString(o[0]));
				}
			}
		}
		return only1List;
	}

	@Override
	public boolean cancelBusiUserClient(CancelBusiUserClientParam param) {
		List<String> only1List = new ArrayList<>();
		List<String> otherList = new ArrayList<>();
		List<String> ids = param.getIds();
		String clientId = param.getClientId();
		//查询所有业务员的user_client_rel
		List<ClientUserRel> rels = clientUserRelResources.getRelByUserIdsClientId(ids, clientId);
		if (ListUntils.isNotNull(rels)) {
			//先查出这些id中所有用户与应用关联只有1个的
			StringBuffer sb = new StringBuffer("select l.user_id,count(DISTINCT l.client_user_rel_id) " +
					"from r_4a_client_user_rel l where l.user_id in :id GROUP BY l.user_id");
			Query q = businessEntity.createNativeQuery(sb.toString());
			q.setParameter("id", ids);
			List<Object[]> oList = q.getResultList();
			if (ListUntils.isNotNull(oList)) {
				for (Object[] o : oList) {
					if (StringUtils.toInteger(o[1]) == 1) {
						only1List.add(StringUtils.toString(o[0]));
					} else {
						otherList.add(StringUtils.toString(o[0]));
					}
				}
			}
		}
		List<BusinessUser> users = null;
		List<BusinessUserVpnRel> vpns = null;
		List<ClientUserRel> clientUser = null;
		List<UserRole> userRoles = null;
		if (ListUntils.isNotNull(only1List)) {
			//删除账号、删除关联、删vpn(引用无效 角色自然失效,应用用户关联自然失效)
			users = businessUserResource.getUserBuIds(only1List);
			vpns = businessuserVpnRelResources.getRelByUserIds(only1List);
			clientUser = clientUserRelResources.getRelByUserIds(only1List);
			clientUserRelResources.delete(clientUser);
			businessUserResource.delete(users);
			ldapApplication.deleteBusinessVpnBatch(vpns);
			businessuserVpnRelResources.delete(vpns);
			userRoles = getUserClientRoles(only1List, clientId);
			userRoleResource.delete(userRoles);
		}
		if (ListUntils.isNotNull(otherList)) {
			//删关联 删该应用对应角色(此时要删除角色)
			clientUserRelResources.delete(rels);
			//查询这些用户在此应用下的所有角色并删除
			userRoles = getUserClientRoles(otherList, clientId);
			userRoleResource.delete(userRoles);
		}
		return true;
	}

	/**
	 * 根据hash获取用户
	 *
	 * @param hash
	 * @return
	 */
	public BusinessUser getBusinessUserByHash(String hash) {
		BusinessUser bu = businessUserResource.getUserByHash(hash);
		return bu;
	}

	private List<UserRole> getUserClientRoles(List<String> otherList, String clientId) {
		StringBuffer sb = new StringBuffer("select ur from UserRole ur,BusinessRole r where ur.roleId = r.id ");
		sb.append(" and ur.userId in :ids and r.clientId = :clientId ");
		Query q = businessEntity.createQuery(sb.toString(), UserRole.class);
		q.setParameter("clientId", clientId);
		q.setParameter("ids", otherList);
		List<UserRole> userRoles = q.getResultList();
		return userRoles;
	}

	/**
	 * 调用简向库查询是否实名认证备案的党员
	 *
	 * @param hash
	 * @return
	 */
	private boolean CheckUserExistFromSimple(String hash) {

		//TODO  上线去掉RestTemplate
		RestTemplate restTemplate = new RestTemplate();

		boolean receiveValue = false;
		HttpEntity<String> entity = new HttpEntity(hash);

		try {
			receiveValue = restTemplate.exchange(checkUserExistUrl, HttpMethod.POST, entity, boolean.class).getBody();
		} catch (Exception e) {
			log.info("校验党员身份失败");
			return receiveValue;
		}
		return receiveValue;
	}

	/**
	 * 根据hashs获取管理员
	 * @param hashs
	 * @return
	 */
	public List<BusinessUser> checkHashAdmin(List<String> hashs) {
		return businessUserResource.checkHashAdmin(hashs);
	}

	public int reNameOrgName(String newOrgName,String orgId){
		if(StringUtils.isNull(newOrgName)){
			return 0;
		}
		if(StringUtils.isNull(orgId)){
			return 0;
		}
		log.info("虚拟网专区改名========>+BusinessUserApplicationImpl=========+reNameOrgName");
		return userRoleResource.renameOrgName(newOrgName,orgId);
	}

}
