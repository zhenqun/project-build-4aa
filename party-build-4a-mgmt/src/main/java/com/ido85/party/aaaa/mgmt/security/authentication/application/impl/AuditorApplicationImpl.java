package com.ido85.party.aaaa.mgmt.security.authentication.application.impl;

import com.ido85.party.aaaa.mgmt.business.application.ClientApplication;
import com.ido85.party.aaaa.mgmt.business.dto.ExportAuthorizationCodeParam;
import com.ido85.party.aaaa.mgmt.business.dto.ExportAuthorizationCodeVo;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.base.ConfigEnum;
import com.ido85.party.aaaa.mgmt.security.authentication.application.*;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.*;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.*;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.RoleResources;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserClientRelResource;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserResources;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserVpnRelResources;
import com.ido85.party.aaaa.mgmt.security.common.LogUtils;
import com.ido85.party.aaaa.mgmt.security.common.UserUtils;
import com.ido85.party.aaaa.mgmt.security.constants.LogConstants;
import com.ido85.party.aaaa.mgmt.security.services.common.CommonApi;
import com.ido85.party.aaaa.mgmt.security.utils.*;
import com.ido85.party.aaaa.mgmt.security.utils.excel.ExportCompareExcel;
import com.ido85.party.aaaa.mgmt.service.RealnameAuthenticaitonClient;
import com.ido85.party.aaaa.mgmt.service.SimpleDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

//import com.ido85.party.aaaa.mgmt.security.authentication.domain.ClientUserRel;
//import com.ido85.party.aaaa.mgmt.security.authentication.repository.ClientMainUserRelResources;
@Named
@Slf4j
public class AuditorApplicationImpl implements AuditorApplication {

	@Inject
	private UserResources userRes;
	
	@Inject
	private UserApplication userApp;
	
	@Inject
	private CommonApi commonApp;
	
	@Value("${ROLE_AUDITOR}")
	private String roleAuditor;
	
	@Value("${ROLE_SECURITY}")
	private String roleSecurity;

	@Inject
	private RoleResources roleResources;

	@PersistenceContext(unitName = "admin")
	private EntityManager adminEntity;
	
	@Inject
	private ConfigApplication configApp;
	
	@Inject
	private GrantLogApplication grantLogApplication;
	
	@Value("${queryUserInfoByHashUrl}")
	private String queryUserInfoByHashUrl;
	
	@Value("${downUrl}")
	private String downUrl;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Inject
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	@Inject
	private UserClientRelResource userClientRelResource;
	
	@Inject
	private CommonApplication commonApplication;
	
	@Inject
	private ClientApplication clientApplication;
	
	@Inject
	private RealnameAuthenticaitonClient realnameAuthenticaitonClient;
	
//	@Inject
//	private ClientMainUserRelResources clientUserRelResources;
	
	@Inject
	private UserVpnRelResources userVpnRelResources;

	@Inject
	private SimpleDataService simpleDataService;

	/**
	 * 开通审计员
	 * @throws Exception 
	 */
	// TODO 待优化，将for循环里面的查询拆出来转成map形式获取 createby:yin
	@Transactional
	@Override
	public Map<String, String> addAuditor(List<InAddSecurityUserDto> param,HttpServletRequest request) throws Exception {
		Map<String,String> result = new HashMap<>();
		StringBuffer message = new StringBuffer();
		List<User> entities = new ArrayList<>();
		List<UserClientRel> rels = new ArrayList<UserClientRel>();
		UserClientRel clientUserRel = null;
		String userId = null;
		UserVpnRel userVpnRel = null;
		User createOr = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(ListUntils.isNotNull(param)){
			//获取当前登录人信息
			User user = UserUtils.getCurrentUser();
			if(null != user){
				userId = user.getId();
			}
			Set<Role> roles;
			Role role;
			for(InAddSecurityUserDto userDto : param){
				String hash = StringUtils.getUserNameIDHash(userDto.getRelName(),userDto.getIdCard());
				//判断用户是不是业务管理员，不能共存
				boolean isBusi = commonApplication.checkIsBusinessUser(hash);
				if(isBusi){
					result.put("flag", "fail");
					result.put("message", "此用户已经为管理员，不能授予审计员角色!");
					return result;
				}
				User u = userApp.getUserByHash(hash);
				if(null != u){
					//判断有没有安全员角色，审计员和安全员不共存
					role = roleResources.getRoleByName(roleSecurity);
					roles = u.getRoles();
					if (null == role || role.getId() < 0) {
						result.put("flag", "fail");
						result.put("message", "角色不存在!");
						return result;
					}
					if(roles.contains(role)){
						result.put("flag", "fail");
						result.put("message", "此用户已经为安全员，不能授予审计员角色!");
						return result;
					}
					String clientId = userDto.getClientId();
					List<UserClientRel> clientRels = userClientRelResource.getRelByUserIdClientId(u.getId(),clientId);
					if(ListUntils.isNotNull(clientRels)){
						result.put("flag", "fail");
						result.put("message", "该用户已经有当前应用的管理权限!");
						return result;
					}
						clientUserRel = new UserClientRel();
						//获取安全员在该应用下的管理范围，赋给审计员，因为安全员开通平级审计员，但是只能开通县级
						UserClientRel securityrels = userClientRelResource.getRelByuIdClientId(userId, clientId);
						if(simpleDataService.checkOrgInWhole(securityrels.getManageId())){
							result.put("flag", "fail");
							result.put("message",securityrels.getManageName()+"正在进行组织关系整建制转接");
							return result;
						}
						if(null != securityrels){
							clientUserRel.setManageId(securityrels.getManageId());
							clientUserRel.setManageName(securityrels.getManageName());
						}
						
						clientUserRel.setClientId(clientId);
						clientUserRel.setId(idGenerator.next());
						clientUserRel.setUserId(u.getId());
						List<UserClientRel> lists = userClientRelResource.getRelByUserIdClientId(u.getId(), clientId);
//						if(ListUntils.isNull(lists)){
//							rels.add(clientUserRel);
//						}
						userClientRelResource.save(clientUserRel);
						message.append(u.getId()).append("|").append(u.getName()).append(",");
						
						role = roleResources.getRoleByName(roleAuditor);

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
					//获取安全员在该应用下的管理范围，赋给审计员，因为安全员开通平级审计员，但是只能开通县级
					String clientId = userDto.getClientId();
					UserClientRel securityrels = userClientRelResource.getRelByuIdClientId(userId, clientId);
					if(simpleDataService.checkOrgInWhole(securityrels.getManageId())){
						result.put("flag", "fail");
						result.put("message",securityrels.getManageName()+"正在进行组织关系整建制转接");
						return result;
					}
					result = realnameAuthenticaitonClient.realnameAuthentication(userDto.getIdCard(),userDto.getRelName());
					if(null != result && result.containsKey("flag")){
						if("fail".equals(result.get("flag")) || "exception".equals(result.get("flag"))){
							return result;
						}
					}
					user = new User();
					String auditorId = StringUtils.toString(idGenerator.next());
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
					user.setId(auditorId);
					user.setTelePhone(userDto.getTelephone());
					if(null != createOr){
						user.setCreateBy(createOr.getId());
					}
					clientUserRel = new UserClientRel();
					if(null != securityrels){
						clientUserRel.setManageId(securityrels.getManageId());
						clientUserRel.setManageName(securityrels.getManageName());
					}
					clientUserRel.setClientId(clientId);
					clientUserRel.setId(idGenerator.next());
					clientUserRel.setUserId(auditorId);
					userClientRelResource.save(clientUserRel);
					message.append(StringUtils.toString(userId)).append("|").append(userDto.getRelName()).append(",");
					
					role = roleResources.getRoleByName(roleAuditor);

					if (null == role || role.getId() < 0) {
						result.put("flag", "fail");
						result.put("message", "角色不存在!");
						return result;
					}
					roles = new HashSet<>();
					roles.add(role);
					user.setRoles(roles);
					entities.add(user);
					//保存vpn账号以及节点
					UserVpnRel rel = userVpnRelResources.getRelByUserId(userId);
					String ou = null;
					if(null != rel){
						ou = rel.getOuName();
					}
					boolean vpnFlag = commonApplication.createVPNForSecurity(auditorId,code,userDto.getTelephone(),userDto.getIdCard(),ou,"2004");
//					userVpnRel = new UserVpnRel(); 
//					userVpnRel.setOuName(ou);
//					userVpnRel.setVpn(userDto.getIdCard());
//					userVpnRel.setUserId(auditorId);
//					userVpnRel.setUserOuId(idGenerator.next());
//					userVpnRelResources.save(userVpnRel);
				}
			}
			userRes.save(entities);
		}
		result.put("flag", "success");
		result.put("message", message.toString().substring(0, message.length()-1));
		return result;
	}

	/**
	 * 审计员查询
	 */
	public List<AuditorDto> securityAuditorQuery(AuditorQueryParam param) {
		//参数
		String securityName = param.getAuditorName();
		String clientId = param.getClientId();
		String state = param.getState();
		Integer pageNo = param.getPageNo();
		Integer pageSize = param.getPageSize();
		String manageId = param.getManageOrgId();
		String isActivation = param.getIsActivation();
		List<AuditorDto> dtoList = null;
//						List<String> userIds = null;
		String usermanageId = null;
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
			manageIds = clientApplication.getChildManageIds(clientId,manageId);
			if(usermanageId.equals(manageId)){
				manageIds.add(usermanageId);
			}
		}
		
		StringBuffer sb = new StringBuffer("SELECT\n" +
				"	u.\"id\",\n" +
				"	u.\"name\",\n" +
				"	u.authorization_code,\n" +
				"	u.id_card,\n" +
				"	u.is_account_locked,\n" +
				"	u.tele_phone,\n" +
				"	u.create_date,\n" +
				"   cur.manage_id,\n"   +
				"   cur.manage_name, " +
				"   u.is_activation\n" +
				"FROM\n" +
				"	t_4a_actors u\n");
				sb.append(
						"LEFT JOIN r_4a_user_role ur ON u.\"id\" = ur.user_id \n");
				sb.append(
						"left join r_4a_user_client cur on u.id=cur.user_id WHERE\n" +
						"	u.is_account_expired = 'f' and cur.client_id = :clientId and ur.role_id=5 ");
				// and cur.manage_id != :usermanageId
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
//		query.setParameter("usermanageId", usermanageId);
		query.setParameter("clientId", clientId);
		query.setFirstResult((pageNo - 1) * pageSize);
		query.setMaxResults(pageSize);
		List<Object[]> objectList = query.getResultList();
		if(null != objectList && objectList.size()>0){
			dtoList = new ArrayList<AuditorDto>();
//							userIds = new ArrayList<String>();
			AuditorDto dto = null;
			for(Object[] object:objectList){
				dto = new AuditorDto();
				dto.setAuthorizationCode(StringUtils.toString(object[2]));
				dto.setAuditorId(StringUtils.toString(object[0]));
				dto.setAuditorName(StringUtils.toString(object[1]));
				dto.setIdCard(StringUtils.toString(object[3]));
				dto.setState(StringUtils.toString(object[4]).equals("false")?"0":"1");
				dto.setTelephone(StringUtils.toString(object[5]));
				dto.setCreateDate(StringUtils.toString(object[6]));
				dto.setManageId(StringUtils.toString(object[7]));
				dto.setManageName(StringUtils.toString(object[8]));
				dto.setIsActivation(StringUtils.toString(object[9]).equals("false")?"0":"1");
				dtoList.add(dto);
//								userIds.add(StringUtils.toString(object[0]));
			}
		}
		return dtoList;	
	}

	/**
	 * 审计员查询数量
	 */
	public Long securityAuditorQueryCnt(AuditorQueryParam param) {
		//参数
		String securityName = param.getAuditorName();
		String clientId = param.getClientId();
		String state = param.getState();
		Integer pageNo = param.getPageNo();
		Integer pageSize = param.getPageSize();
		String manageId = param.getManageOrgId();
		String isActivation = param.getIsActivation();
		List<AuditorDto> dtoList = null;
//						List<String> userIds = null;
		String usermanageId = null;
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
			manageIds = clientApplication.getChildManageIds(clientId,manageId);
			if(usermanageId.equals(manageId)){
				manageIds.add(usermanageId);
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
						"	u.is_account_expired = 'f' and cur.client_id = :clientId and ur.role_id=5 ");
		// and cur.manage_id != :usermanageId
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
//		query.setParameter("usermanageId", usermanageId);
		query.setParameter("clientId", clientId);
		return StringUtils.toLong(query.getSingleResult());
	}

	/**
	 * 审计员状态修改
	 * @throws Exception 
	 */
	public Map<String, String> modifyAuditorState(ModifyAuditorStatusParam param,HttpServletRequest request) throws Exception {
		User currentUser = UserUtils.getCurrentUser();
		Map<String, String> map = new HashMap<String,String>();
		List<String> ids = param.getAuditorUserIds();
		final List<User> users = userRes.getUserBuIds(ids);
		String state = param.getState();
		Boolean s = null;
		if("0".equals(state)){
			s = false;
		}
		if("1".equals(state)){
			s = true;
		}
		userRes.modifyBusinessUsersStatus(ids, s);
		//增加日志
		GrantLog grantLog = LogUtils.CreateGrantLog(request);
		threadPoolTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					grantLogApplication.addLog(currentUser,grantLog,LogConstants.MOD_AUDITOR_STATE,LogConstants.MOD_AUDITOR_STATE_NAME,"1","修改成功",param,users);
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
	 * 审计员注销
	 * @throws Exception 
	 */
	public Map<String, String> cancellationSecurityUser(CancellationAuditorParam param,HttpServletRequest request) throws Exception {
		User currentUser = UserUtils.getCurrentUser();
		Map<String, String> map = new HashMap<String,String>();
		List<String> ids = param.getAuditorUserIds();
		List<User> users = userRes.getUserBuIds(ids);
		userRes.cancellationBusinessUser(true,ids);
		//增加日志
		GrantLog grantLog = LogUtils.CreateGrantLog(request);
		threadPoolTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					grantLogApplication.addLog(currentUser,grantLog,LogConstants.CANCEL_AUDITOR,LogConstants.CANCEL_AUDITOR_NAME,"1","注销成功",param,users);
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
	 * 导出授权码 
	 * @throws Exception 
	 */
	public String exportAuthorizationCode(ExportAuthorizationCodeParam param,HttpServletRequest request) throws Exception {
		User currentUser = UserUtils.getCurrentUser();
		List<String> ids = param.getIds();
		List<User> users = userRes.getUserBuIds(ids);
		ExportCompareExcel detail = new ExportCompareExcel("",ExportAuthorizationCodeVo.class,1,StringUtils.toString(ids.size()));
		List<ExportAuthorizationCodeVo> voList = getExportAuthorizationCode(ids);
//		excel名字生成
		String fileName = "审计员授权码_"+DateUtils.getDate("yyyyMMddHHmmss")+"_"+StringUtils.getRandomNum(3)+".xls";
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
//					grantLogApplication.addLog(currentUser,grantLog,LogConstants.EXPORT_AUDITOR_CODE,LogConstants.EXPORT_AUDITOR_CODE_NAME,"1","导出成功",users,downUrl+fileName);
//				} catch (Exception e) {
//					log.error(e.getMessage());
//				}
//			}
//		});
		return downUrl+fileName;
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

	/**
	 * 设置范围
	 */
	public Map<String,String> setManageScope(List<SetAuditorManageScopeParam> param,HttpServletRequest request) {
//		User currentUser = UserUtils.getCurrentUser();
		String userId = null;
		String manageOrgId = null;
		String manageOrgName = null;
		String clientId = null;
		List<UserClientRel> rels = null;
		UserClientRel rel = null;
		Map<String, String> map = new HashMap<String,String>();
		if(ListUntils.isNotNull(param)){
			rels = new ArrayList<>();
			for(SetAuditorManageScopeParam dto:param){
				userId = dto.getAuditorUserId();
				manageOrgId = dto.getManageOrgId();
				manageOrgName = dto.getManageOrgName();
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


}
