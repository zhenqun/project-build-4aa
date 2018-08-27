package com.ido85.party.aaaa.mgmt.security.authentication.application.impl;

import com.ido85.party.aaaa.mgmt.business.application.ClientApplication;
import com.ido85.party.aaaa.mgmt.business.domain.*;
import com.ido85.party.aaaa.mgmt.business.dto.CheckBusinessUserParam;
import com.ido85.party.aaaa.mgmt.business.dto.ExportAuthorizationCodeParam;
import com.ido85.party.aaaa.mgmt.business.dto.ExportAuthorizationCodeVo;
import com.ido85.party.aaaa.mgmt.business.resources.*;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.OrganizationDto;
import com.ido85.party.aaaa.mgmt.dto.assist.*;
import com.ido85.party.aaaa.mgmt.dto.base.ConfigEnum;
import com.ido85.party.aaaa.mgmt.dto.userinfo.AssistClientDto;
import com.ido85.party.aaaa.mgmt.ldap.application.LdapApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.*;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.*;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.*;
import com.ido85.party.aaaa.mgmt.security.common.BaseApplication;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import com.ido85.party.aaaa.mgmt.business.domain.ClientUserRel;

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
public class AssistUserApplicationImpl  extends BaseApplication implements AssistUserApplication {
	@PersistenceContext(unitName = "admin")
	private EntityManager adminEntity;
	@PersistenceContext(unitName = "business")
	private EntityManager businessEntity;

	@Inject
	private UserApplication userApplication;
	@Inject
	private IdGenerator idGenerator;
	@Inject
	private UserClientRelResource userClientRelResource;
	@Inject
	private ClientUserRelResources clientUserRelResources;
	@Inject
	private CommonApplication commonApplication;
	@Inject
	private CLientExpandResources cLientExpandResources;
	@Inject
	private RestTemplate restTemplate;
	@Inject
	private BusinessUserResources businessUserResource;
	@Value("${ROLE_AUDITOR}")
	private String roleAuditor;
	@Inject
	private RoleResources roleResources;
	@Inject
	private RealnameAuthenticaitonClient realnameAuthenticaitonClient;
	@Value("${checkUserExistUrl}")
	private String checkUserExistUrl;//调用简向库地址（查询是否实名党员信息）
	@Inject
	private BusinessuserVpnRelResources businessuserVpnRelResources;
	@Inject
	private LdapApplication ldapApplication;
	@Inject
	private UserRoleResource userRoleResource;
	@Inject
	private AssistManageResources assistManageResources;
	@Inject
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	@Inject
	private ClientApplication clientApplication;
	@Inject
	private GrantLogApplication grantLogApplication;
	@Inject
	private ApplyUserRepository applyUserRepository;
	@Inject
	private ApplyUserBasicResources applyUserBasicResources;
	@Value("${CLIENT_MULTI}")
	private String clientMulti;
	@Inject
	private ConfigApplication configApp;
	@Value("${downUrl}")
	private String downUrl;
	@Inject
	private SimpleDataService simpleDataService;

	@Override
	public List<AssistClientDto> getAssistUserManage() {
		List<OrganizationDto> orgDtoList = new ArrayList<OrganizationDto>();
		List<UserClientRel> assistapplications = null;
		List<AssistClientDto> resultList = new ArrayList<>();
		String currentUserId = null;
		ClientExpand ce = null;
		//获取当前登录用户信息
		User user = UserUtils.getCurrentUser();
		String level=null;
		if (null != user) {
			currentUserId = user.getId();
			//获取当前用户的所有应用
			assistapplications = userClientRelResource.getAssistAllApplication(currentUserId);
			if (null != assistapplications && assistapplications.size() > 0) {
				//TODO 优化调用党组织树   多个应用查询是否相同
				for (UserClientRel userClientRel : assistapplications) {
					//查询该应用的url
					ce = cLientExpandResources.getClientById(userClientRel.getClientId());
					if (null != ce) {
						String url = ce.getLevelUrl();
						String isEureka = ce.getIsEureka();
						//调用党组织树
						try{
							orgDtoList = getTreeByParam(isEureka, url, userClientRel.getManageId());
						}catch (Exception e){
//							e.printStackTrace();
							log.error("辅助安全员模块调用"+userClientRel.getClientId()+"应用失败，请求继续....");
							continue;
						}
						if (null != orgDtoList && orgDtoList.size() > 0) {
							//是否县级
							if (3 == orgDtoList.get(0).getLevel() || "3".equals(orgDtoList.get(0).getLevel())) {
								AssistClientDto dto = new AssistClientDto();
								dto.setClientId(userClientRel.getClientId());
								dto.setClientName(getClientNameById(userClientRel.getClientId()));
								dto.setOrgId(userClientRel.getManageId());
								resultList.add(dto);
							}
						}
					}
				}
			}

		}
		return resultList;

	}

	/**
	 * 调用党组织树
	 *
	 * @param isEureka
	 * @param url
	 * @param manageId
	 * @return
	 */
	private List<OrganizationDto> getTreeByParam(String isEureka, String url, String manageId) {
		List<OrganizationDto> orgDtoList = new ArrayList<OrganizationDto>();
		//如果是服务发现
		if ("1".equals(isEureka)) {
			orgDtoList = Arrays.asList(restTemplate.getForObject(url + manageId, OrganizationDto[].class));
		}
		if ("0".equals(isEureka)) {
			RestTemplate restTemplat2 = new RestTemplate();
			if (!StringUtils.isNull(manageId)) {
				orgDtoList = Arrays.asList(restTemplat2.getForObject(url + manageId, OrganizationDto[].class));
			}
		}
		return orgDtoList;
	}

	/**
	 * 使用clientid查询client名称
	 *
	 * @param clientId
	 * @return
	 */
	private String getClientNameById(String clientId) {
		String clientSql = "select o.client_name from client_expand o where o.client_id = :clientId";
		Query clientQuery = businessEntity.createNativeQuery(clientSql);
		clientQuery.setParameter("clientId", clientId);
		List<Object[]> clientList = clientQuery.getResultList();
		if (null != clientList && clientList.size() > 0) {
			return StringUtils.toString(clientList.get(0));
		}
		return null;
	}

	/**
	 * 17地市的县级
	 *
	 * @return
	 */
	@Override
	public boolean getAssistIsModule() {
		Set<UserClientRel> userClientRelList = null;
		//获取创建者信息
		User user = UserUtils.getCurrentUser();
		List<String> manageIds = null;
		List<String> clients = null;
		String manageId = null;
		String client = null;
		boolean existFlag=false;
		//读取配置文件中组织信息
		String clientsFromPro=clientMulti;
		List<String> clientsFrom=getClientsFromPro(clientsFromPro);
		if(ListUntils.isNull(clientsFrom)){
			return false;
		}
		if (null != user) {
			//获取创建者应用和管理范围
			userClientRelList = userClientRelResource.getSetClientRelByUserId(user.getId());
			if (null != userClientRelList && userClientRelList.size() > 0) {
				manageIds = new ArrayList<>();
				clients = new ArrayList<>();
				for (UserClientRel ucr : userClientRelList) {
					manageId = ucr.getManageId();
					manageIds.add(manageId);
					client = ucr.getClientId();
					if(clientsFrom.contains(client)){
						existFlag=true;
					}
					clients.add(client);
				}
			}
			StringBuffer sb = new StringBuffer("select a.id from  AssistCounty a  where 1= 1 ");
			if(!existFlag) {
				if (ListUntils.isNotNull(clients)) {
					sb.append(" and a.clientId in :clients");
				}
			}
			if (ListUntils.isNotNull(manageIds)) {
				sb.append(" AND  a.manageId in :manageIds");
			}
			Query query = adminEntity.createQuery(sb.toString());
			if(!existFlag) {
				if (ListUntils.isNotNull(clients)) {
					query.setParameter("clients", clients);
				}
			}
			if (ListUntils.isNotNull(manageIds)) {
				query.setParameter("manageIds", manageIds);
			}
			List<Object[]> oList = query.getResultList();
			if (ListUntils.isNotNull(oList) && oList.size() > 0) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 开通辅助安全员
	 *
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, String> addAssistSecurityUser(@Valid List<InAddAssistUserDto> param, HttpServletRequest request) throws Exception {
		Map<String, String> result = new HashMap<>();
		//对传入参数进行非空校验检测
		result = checkParam(param);
		if (result.containsKey("flag") && "fail".equals(result.get("flag"))) {
			result.put("message", "传入数据非法!");
			return result;
		}
		StringBuffer message = new StringBuffer();
		List<BusinessUser> entities = new ArrayList<>();
		UserClientRel clientUserRel = null;
		UserVpnRel userVpnRel = null;
		//应用和管理范围信息
		AddAssistUserScopeParam scope = null;
		//获取当前登录安全员
		User createOr = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (null == createOr) {
			result.put("flag", "fail");
			result.put("message", "获取创建者信息错误，请联系管理员");
			return result;
		}
		if (ListUntils.isNotNull(param)) {
			BusinessUser user = null;
			String clientId = null;
			for (InAddAssistUserDto userDto : param) {
				//身份hash
				//当创建辅助安全员时，既不能为业务管理员，也不能为安全员和审计员
				String hash = StringUtils.getUserNameIDHash(userDto.getRelName().trim(), userDto.getIdCard().trim());
				boolean isBusinessUser = commonApplication.checkIsBusinessUser(hash);
				if (isBusinessUser) {
					result.put("flag", "fail");
					result.put("message", " 此用户已经是安全员，或辅助安全员，或业务系统管理员，或审计管理员，所以您不能再给其开通辅助安全员");
					return result;
				}
				boolean isSecOrAu = commonApplication.checkIsSecOrAudi(hash);
				if (isSecOrAu) {
					result.put("flag", "fail");
					result.put("message", " 此用户已经是安全员，或辅助安全员，或业务系统管理员，或审计管理员，所以您不能再给其开通辅助安全员");
					return result;
				}
				BusinessUser u = businessUserResource.getUserByHash(hash);
				if (null != u) {
					//如果已经存在该辅助安全员
					result.put("flag", "fail");
					result.put("message", "该辅助安全员已经存在");
					return result;
				} else {
					return this.addAssistUser(userDto, hash, createOr);
				}

			}
		}
		result.put("flag", "success");
		result.put("message", message.toString().substring(0, message.length() - 1));
		return result;
	}

	/**
	 * 添加辅助安全员(实现事务控制范围缩小)
	 *
	 * @param userDto
	 * @param hash
	 * @param createOr
	 * @return
	 */
	@Transactional
	public Map<String, String> addAssistUser(InAddAssistUserDto userDto, String hash, User createOr) {
		//SSO-VPN
		BusinessUser user = new BusinessUser();
		boolean checkResult = false;
		StringBuffer idCards = new StringBuffer();
		Map<String, String> result = new HashMap<String, String>();
		List<BusinessUser> entities = new ArrayList<BusinessUser>();
		StringBuffer message = new StringBuffer();
		ClientUserRel cur = null;
		//辅助安全员必须为实名认证备案的党员
		checkResult = CheckUserExistFromSimple(hash);
		if (!checkResult) {
			result.put("flag", "fail");
			result.put("message", "辅助安全员必须为实名认证的党员");
			return result;
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
		user.setTelePhone(userDto.getTelephone());
		user.setHash(hash);
		user.setIdCard(userDto.getIdCard());
		user.setName(userDto.getRelName());
		if (null != createOr) {
			user.setCreateBy(createOr.getId());
		}
		entities.add(user);

		idCards.append(userDto.getIdCard() + " ");

		Set<String> ouList = new HashSet<>();
		UserRole ur = null;
		AssistManage assistManage = null;
		List<UserRole> userRoleList = new ArrayList<>();
		List<ClientUserRel> curList = new ArrayList<>();
		List<AssistManage> assistManageList = new ArrayList<>();
		String client = null;

		cur = new ClientUserRel();
		cur.setClientId("party-build-assist-ui");
		cur.setClientUserRelId(idGenerator.next());
		cur.setUserId(userId);
		curList.add(cur);

		for (AddAssistUserScopeParam scopreParam : userDto.getClients()) {
			//使用Set存储ou节点
			ouList.add(scopreParam.getOuName());
			//保存角色
			ur = new UserRole();
			ur.setId(idGenerator.next());
			ur.setManageId(scopreParam.getManageId());
			ur.setManageName(scopreParam.getManageName());
			ur.setManageCode(scopreParam.getManageCode());
			ur.setRoleId(8L);
			ur.setUserId(userId);
			userRoleList.add(ur);

			//保存辅助安全员管理应用表
			assistManage = new AssistManage();
			assistManage.setAssistManageId(StringUtils.toString(idGenerator.next()));
			assistManage.setFzuserId(userId);
			assistManage.setRoleId(8L);
			assistManage.setClientId(scopreParam.getClientId());
			assistManage.setManageId(scopreParam.getManageId());
			assistManage.setManageName(scopreParam.getManageName());
			assistManage.setManageCode(scopreParam.getManageCode());
			assistManage.setCreateBy(createOr.getId());
			assistManage.setCreateDate(new Date());
			assistManage.setCreateManageId(scopreParam.getCreateManageId());
			if (!StringUtils.isNull(scopreParam.getManageCode())) {
				assistManage.setCreateManageCode(scopreParam.getManageCode());
			}
			if (!StringUtils.isNull(scopreParam.getCreateManageName())) {
				assistManage.setCreateManageName(scopreParam.getCreateManageName());
			}
			assistManageList.add(assistManage);
		}
		String ouToSVN = null;
		if(ouList.size()>2){
			result.put("flag", "fail");
			result.put("message", "节点数据异常");
			return result;
		}

		if (ouList.size() != 1) {
			for (String ouu : ouList){
				if(!"node0".equals(ouu)){
					ouToSVN = ouu;
				}
			}
		}
		else{
			for (String ou : ouList) {
				ouToSVN = ou;
			}
		}
		if (StringUtils.isNull(ouToSVN)) {
			result.put("flag", "fail");
			result.put("message", "节点数据异常");
			return result;
		}

		businessUserResource.save(entities);
		userRoleResource.save(userRoleList);
		clientUserRelResources.save(curList);
		assistManageResources.save(assistManageList);


		//查询是否已经存在vpn   如果存在更新userId
		BusinessUserVpnRel vpnUser = null;
		vpnUser = businessuserVpnRelResources.getVpnUserByIdCard(userDto.getIdCard());
		if (null == vpnUser) {
			boolean vpnFlag = commonApplication.createVPN(userId, code, userDto.getTelephone(), userDto.getIdCard(), ouToSVN);
			log.info("创建VPN账号结果=======" + vpnFlag);
		}else{
				String ouName = vpnUser.getOu();
				//如果原来账号在node0共享节点，新的账号在节点应用，则将vpn账号移动到节点区
				if ("node0".equals(ouName) && !"node0".equals(ouToSVN)) {
					ldapApplication.changeVPNOuPwd(ouName, vpnUser.getVpn(), ouToSVN, userId);
				} else if (!"node0".equals(ouToSVN) && !ouName.equals(ouToSVN)) {
					result.put("flag", "fail");
					result.put("message", "您选择的管理范围与原有管理范围不在同一节点");
					return result;
				}
		}
		log.info("此次开通辅助安全员账号为======:" + userDto.getIdCard() + "姓名为:" + userDto.getRelName());
		message.append(userId).append("|").append(userDto.getRelName()).append(",");
		result.put("flag", "success");
		result.put("message", message.toString().substring(0, message.length() - 1));
		return result;
	}

	/**
	 * 对传入的参数进行非空校验
	 *
	 * @param param
	 * @return
	 */
	private Map<String, String> checkParam(List<InAddAssistUserDto> param) {
		Map<String, String> result = new HashMap<>();
		String clientId = null;
		List<AddAssistUserScopeParam> scope = null;
		String manageId = null;
		String manageCode = null;
		String manageName = null;
		//添加创建者管理范围id
		String createMangeId=null;
		String ou = null;
		for (InAddAssistUserDto u : param) {
			scope = u.getClients();
			for (AddAssistUserScopeParam scopeParam : scope) {
				manageId = scopeParam.getManageId();
				manageCode = scopeParam.getManageCode();
				manageName = scopeParam.getManageName();
				ou = scopeParam.getOuName();
				createMangeId=scopeParam.getCreateManageId();
				if (StringUtils.isNull(manageId) || StringUtils.isNull(manageCode)
						|| StringUtils.isNull(manageName) || StringUtils.isNull(ou)||StringUtils.isNull(createMangeId)) {
					result.put("flag", "fail");
					result.put("message", "请求参数管理范围或者应用参数为空!");
					return result;
				}
			}
			if (StringUtils.isNull(u.getIdCard())) {
				result.put("flag", "fail");
				result.put("message", "身份证号不能为空!");
				return result;
			}
			if (StringUtils.isNull(u.getRelName())) {
				result.put("flag", "fail");
				result.put("message", "真实姓名不能为空!");
				return result;
			}
			if (!StringUtils.isTelephone(u.getTelephone())) {
				result.put("flag", "fail");
				result.put("message", "手机号非法!");
				return result;
			}
			if(simpleDataService.checkOrgInWhole(manageId)){
				result.put("flag", "fail");
				result.put("message",manageName+"正在进行组织关系整建制转接");
				return result;
			}
		}
		result.put("flag", "success");
		return result;
	}


	/**
	 * 检测该组织在该应用下安全员数量
	 *
	 * @param clientId
	 * @param manageOrgId
	 * @return
	 */
	private Long checkOrgAssistUserCnt(String clientId, String manageOrgId) {
		StringBuffer sb = new StringBuffer("select count(*) from t_4a_actors t,r_4a_user_client l,r_4a_user_role r where " +
				"t.id=l.user_id and t.id=r.user_id and l.client_id = :clientId and l.manage_id = :manageId and r.role_id = '4'");
		Query q = adminEntity.createNativeQuery(sb.toString());
		q.setParameter("clientId", clientId);
		q.setParameter("manageId", manageOrgId);
		Long cnt = StringUtils.toLong(q.getSingleResult());
		return cnt;
	}


	/**
	 * 调用简向库查询是否实名认证备案的党员
	 *
	 * @param hash
	 * @return
	 */
	private boolean CheckUserExistFromSimple(String hash) {

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


	@Override
	public List<String> checkAssistUserExists(CheckBusinessUserParam param) {
		List<String> result = null;
		String idCard = null;
		String name = null;
		List<String> hashs = null;
		List<BusinessUser> userList = null;
		if (null != param.getName() && null != param.getIdCard()) {
			idCard = param.getIdCard();
			name = param.getName();
			hashs.add(StringUtils.getUserNameIDHash(name, idCard));
			userList = getUserListByHashsAndClientId(hashs);
			if (ListUntils.isNotNull(userList)) {
				for (BusinessUser u : userList) {
					result.add(u.getIdCard() + "|" + u.getName());
				}
			}
		}
		return result;
	}

	/**
	 * 辅助安全员授权
	 *
	 * @param param
	 * @param request
	 * @return
	 */
	@Override
	@Transactional
	public Map<String, String> grantAssistUser(List<GrantAssistUserParam> param, HttpServletRequest request) {
		UserRole ur = null;
		AssistManage assistManage = null;
		ClientUserRel cur = null;
		List<UserRole> userRoleList = new ArrayList<>();
		List<UserRole> olduserRoleList = new ArrayList<>();
		List<AssistManage> assistManageList = new ArrayList<>();
		List<AssistManage> oldassistManageList = new ArrayList<>();
		Map<String, String> map = new HashMap<String, String>();
		map = checkGrantParam(param);
		String fzuserId = null;
		if (map.containsKey("flag") && "fail".equals(map.get("flag"))) {
			map.put("message", "请求数据异常!");
			return map;
		}
		User createOr = UserUtils.getCurrentUser();
		for (GrantAssistUserParam grantAssistUserParam : param) {

			fzuserId = grantAssistUserParam.getFzuserId();
			olduserRoleList = getUserRoleByClientUser(fzuserId);
			oldassistManageList = getUserAssistByUser(fzuserId);
			userRoleResource.delete(olduserRoleList);
			assistManageResources.delete(oldassistManageList);

			if (ListUntils.isNotNull(grantAssistUserParam.getClients())) {
				for (GrantAssistClients clients : grantAssistUserParam.getClients()) {
					ur = new UserRole();
					ur.setId(idGenerator.next());
					ur.setManageId(clients.getManageId());
					ur.setManageName(clients.getManageName());
					ur.setManageCode(clients.getManageCode());
					ur.setRoleId(8L);
					ur.setUserId(fzuserId);
					userRoleList.add(ur);
					//保存辅助安全员管理应用表
					assistManage = new AssistManage();
					assistManage.setAssistManageId(StringUtils.toString(idGenerator.next()));
					assistManage.setFzuserId(fzuserId);
					assistManage.setRoleId(8L);
					assistManage.setClientId(clients.getClientId());
					assistManage.setManageId(clients.getManageId());
					assistManage.setManageName(clients.getManageName());
					assistManage.setManageCode(clients.getManageCode());
					if (null != createOr) {
						assistManage.setCreateBy(createOr.getId());
					}
					if (null != clients.getCreateManageId()) {
						assistManage.setCreateManageId(clients.getCreateManageId());
					}
					if (null != clients.getCreateManageCode()) {
						assistManage.setCreateManageCode(clients.getCreateManageCode());
					}
					if (null != clients.getCreateManageName()) {
						assistManage.setCreateManageName(clients.getCreateManageName());
					}
					assistManage.setCreateDate(new Date());
					assistManageList.add(assistManage);
				}
			}
			userRoleResource.save(userRoleList);
//			clientUserRelResources.save(curList);
			assistManageResources.save(assistManageList);
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


	private List<UserRole> getUserRoleByClientUser(String userId) {
		StringBuffer sb = new StringBuffer("select ur from UserRole ur where ur.userId= :userId  ");
		Query q = businessEntity.createQuery(sb.toString(), UserRole.class);
		q.setParameter("userId", userId);
		List<UserRole> userRoleList = q.getResultList();
		return userRoleList;
	}

	private List<AssistManage> getUserAssistByUser(String fzuserId) {
		StringBuffer sb = new StringBuffer("select ur from AssistManage ur where ur.fzuserId= :fzuserId ");
		Query q = businessEntity.createQuery(sb.toString(), AssistManage.class);
		q.setParameter("fzuserId", fzuserId);
		List<AssistManage> userRoleList = q.getResultList();
		return userRoleList;
	}

	/**
	 * 授权前的应用范围查询
	 *
	 * @param fzuserId
	 * @return
	 */
	@Override
	public List<GrantAssistClients> getAssistUserClientAndManage(AssistUserIdDto fzuserId) {
		List<GrantAssistClients> assistClients = null;
		GrantAssistClients clients = null;
		AssistManage assistManage = null;
		StringBuffer sb = new StringBuffer("select  r.clientId ,r.manageCode,r.manageId,r.manageName ," +
				"  r.createManageId ,r.createManageName, r.createManageCode from AssistManage r where r.fzuserId  =:fzuserId and r.delFlag='0'");
		Query q = businessEntity.createQuery(sb.toString());
		q.setParameter("fzuserId", fzuserId.getFzuserId());
		List<Object[]> oList = q.getResultList();
		if (ListUntils.isNotNull(oList)) {
			assistClients = new ArrayList<>();
			for (Object[] o : oList) {
				clients = new GrantAssistClients();
				clients.setClientId(StringUtils.toString(o[0]));
				clients.setManageCode(StringUtils.toString(o[1]));
				clients.setManageId(StringUtils.toString(o[2]));
				clients.setManageName(StringUtils.toString(o[3]));
				clients.setCreateManageId(StringUtils.toString(o[4]));
				clients.setCreateManageName(StringUtils.toString(o[5]));
				clients.setCreateManageCode(StringUtils.toString(o[6]));
				assistClients.add(clients);
			}
		}
		return assistClients;
	}


	//授权参数校验
	private Map<String, String> checkGrantParam(List<GrantAssistUserParam> param) {
		Map<String, String> result = new HashMap<>();
		String userId = null;
		String clientId = null;
		String manageName = null;
		String manageCode = null;
		String manageId = null;
		if (ListUntils.isNull(param)) {
			result.put("flag", "fail");
			return result;
		}
		for (GrantAssistUserParam p : param) {
			userId = p.getFzuserId();
			for (GrantAssistClients clients : p.getClients()) {
				clientId = clients.getClientId();
				manageCode = clients.getManageCode();
				manageId = clients.getManageId();
				manageName = clients.getManageName();
				if (StringUtils.isNull(clientId) ||
						StringUtils.isNull(manageName) ||
						StringUtils.isNull(manageCode) ||
						StringUtils.isNull(manageId)) {
					result.put("flag", "fail");
					return result;
				}
				if(simpleDataService.checkOrgInWhole(manageId)){
					result.put("flag", "fail");
					result.put("message",manageName+"正在进行组织关系整建制转接");
					return result;
				}
			}

		}
		result.put("flag", "success");
		return result;
	}


	private List<BusinessUser> getUserListByHashsAndClientId(List<String> hashs) {
		StringBuffer sb = new StringBuffer();
		sb.append("select u from BusinessUser u ,ClientUserRel l where u.id = l.userId");
		if (ListUntils.isNotNull(hashs)) {
			sb.append(" and u.hash in :hashs");
		}
		Query q = businessEntity.createQuery(sb.toString(), BusinessUser.class);
		if (ListUntils.isNotNull(hashs)) {
			q.setParameter("hashs", hashs);
		}
		List<BusinessUser> userList = q.getResultList();
		return userList;
	}


	/**
	 * 撤销辅助安全员(批量)
	 *
	 * @param fzuseIds
	 * @return
	 */
	@Override
	@Transactional
	public Map<String,String> cancelAssistUserClient(AssistUserMultiDto fzuseIds) {
		Map<String,String> map = new HashMap<>();
		List<BusinessUser> usersList = new ArrayList<>();
		List<BusinessUserVpnRel> vpns = new ArrayList<>();
		List<ClientUserRel> oldcurList = new ArrayList<>();
		List<String>  idCards= new ArrayList<>();

		List<UserRole> olduserRoleList = new ArrayList<>();
		List<AssistManage> oldassistManageList = new ArrayList<>();
		if (ListUntils.isNotNull(fzuseIds.getFzuserIds())) {
		try{
			usersList = businessUserResource.getUserBuIds(fzuseIds.getFzuserIds());
			if(ListUntils.isNull(usersList)){
				map.put("flag", "fail");
				map.put("message", "数据异常，不存在该辅助安全员信息");
				return map;
			}
			businessUserResource.delete(usersList);

			vpns = businessuserVpnRelResources.getRelByUserIds(fzuseIds.getFzuserIds());
			businessuserVpnRelResources.delete(vpns);

			oldcurList = clientUserRelResources.getRelByUserIds(fzuseIds.getFzuserIds());
			clientUserRelResources.delete(oldcurList);

			oldassistManageList = assistManageResources.getAssistByUserIds(fzuseIds.getFzuserIds());
			assistManageResources.delete(oldassistManageList);

			olduserRoleList = userRoleResource.getRelByUserId(fzuseIds.getFzuserIds());
			userRoleResource.delete(olduserRoleList);

			ldapApplication.deleteBusinessVpnBatch(vpns);

			}catch (Exception e){
			e.printStackTrace();
				map.put("flag", "fail");
				map.put("message", "撤销失败!");
				return map;
			}
		}
		map.put("flag", "success");
		map.put("message", "撤销成功!");
		return map;
	}

	/**
	 * 修改辅助安全员状态(0 禁用 1启用)
	 * @param param
	 * @param request
	 * @return
	 */
	@Override
	public Map<String, String> modifyAssistUserStatus(UpdateAssistStateDto param, HttpServletRequest request) {
		User currentUser = UserUtils.getCurrentUser();
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer idCards = new StringBuffer();
		List<String> ids = param.getFzuserIds();
		List<BusinessUser> users = businessUserResource.getUserByIds(ids);
		for (String id : ids) {
			idCards.append(id + " ");
		}
		String state = param.getState();
		Boolean s = null;
		if ("0".equals(state)) {
			s = true;
		}
		if ("1".equals(state)) {
			s = false;
		}
		int cnt = businessUserResource.modifyBusinessUsersStatus(ids, s);
		if (cnt <= 0) {
			map.put("flag", "fail");
			map.put("message", "修改失败!");
			return map;
		}
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
		map.put("message", "状态修改成功!");
		return map;
	}


	/**
	 * 辅助安全员查询
	 *
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<AssistUserDto> getAssistUserByCondition(AssistUserQueryDto param) throws Exception {
		String relName = param.getRelName().trim();
		String idCard = param.getIdCard().trim();
		String telephone = param.getTelephone().trim();
		String state = param.getState().trim();
		String isActivation = param.getIsActive();
		Integer pageNo = param.getPageNo();
		Integer pageSize = param.getPageSize();
		List<AssistUserDto> dtoList = null;
		AssistUserDto dto = null;
		List<AssistClientsQueryResultDto> clients = null;
		Set<UserClientRel> userClientRelList = null;
		//查询level为3的下面所有
		//根据clientid查询分级管理的地址,根据地址获取所有下级id
		//获取当前用户
		String createId = null;
		User user = UserUtils.getCurrentUser();
		createId = user.getId();
		//创建Set存储manageId，去掉重复数据
		Set manageIdss = null;
		//创建List把Set值插入
		List<String> manageIds = new ArrayList<>();
		String manageId = null;
		//通过创建者ID查询应用和管理范围
		if (StringUtils.isNull(createId)) {
			log.info("获取创建者信息失败");
			return null;
		}
		userClientRelList = userClientRelResource.getSetClientRelByUserId(createId);
		if (null != userClientRelList && userClientRelList.size() > 0) {
			manageIdss = new HashSet();
			for (UserClientRel ucr : userClientRelList) {
				manageId = ucr.getManageId();
				manageIdss.add(manageId);
			}
		}
		StringBuffer sb = new StringBuffer("SELECT  DISTINCT " +
				"	u.\"id\",\n" +
				"	u.\"name\",\n" +
				"	u.authorization_code,\n" +
				"	u.id_card,\n" +
				"	u.tele_phone,\n" +
				"	u.is_account_locked,\n" +
				"	u.create_date,\n" +
				"   u.is_activation\n" +
				"FROM\n" +
				"	t_4a_actors u , tf_f_assist_manage t  ");
		sb.append(" where    u.id=t.fz_user_id   ");
		//将set插入list
		manageIds.addAll(manageIdss);
		if (ListUntils.isNotNull(manageIds)) {
			sb.append("  and t.create_manage_id  in :manageIds");
		}
		sb.append(" ");
		if (!StringUtils.isNull(relName)) {
			sb.append(" and u.\"name\" like :name ");
		}
		if (!StringUtils.isNull(telephone)) {
			sb.append(" and u.\"tele_phone\" like :telephone ");
		}
		//0----禁用  1----启用
		if (!StringUtils.isNull(state)) {
			sb.append(" and u.is_account_locked = :state ");
		}
		if (!StringUtils.isNull(idCard)) {
			sb.append(" and u.\"id_card\" like :idCard ");
		}
		if (!StringUtils.isNull(isActivation)) {
			sb.append(" and u.is_activation = :isActivation ");
		}
		sb.append(" order by u.create_date desc ");
		Query query = businessEntity.createNativeQuery(sb.toString());
		if (!StringUtils.isNull(relName)) {
			query.setParameter("name", "%" + relName + "%");
		}
		if (!StringUtils.isNull(idCard)) {
			query.setParameter("idCard", "%" + idCard + "%");
		}
		if (!StringUtils.isNull(telephone)) {
			query.setParameter("telephone", "%" + telephone + "%");
		}

		if (!StringUtils.isNull(state)) {
			boolean stateFlag = true;
			if ("1".equals(state)) {
				stateFlag = false;
			}
			if ("0".equals(state)) {
				stateFlag = true;
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
		query.setFirstResult((pageNo - 1) * pageSize);
		query.setMaxResults(pageSize);
		List<Object[]> objectList = query.getResultList();

		if (null != objectList && objectList.size() > 0) {
			dtoList = new ArrayList<AssistUserDto>();
//			StringBuffer stringBuffer = new StringBuffer("select  s.manage_name  " +
//					" from tf_f_assist_manage s where s.fz_user_id = :userId ");
			for (Object[] object : objectList) {
				dto = new AssistUserDto();
				dto.setFzuserId(StringUtils.toString(object[0]));
				dto.setRelName(StringUtils.toString(object[1]));
				dto.setAuthorizationCode(StringUtils.toString(object[2]));
				dto.setIdCard(StringUtils.toString(object[3]));
				dto.setTelephone(StringUtils.toString(object[4]));
				dto.setState(StringUtils.toString(object[5]).equals("true") ? "0" : "1");
				dto.setCreateDate(StringUtils.toString(object[6]));
				dto.setIsActive(StringUtils.toString(object[7]).equals("false") ? "0" : "1");

				if (null != StringUtils.toString(object[0])) {
					clients = this.queryClientsByFzUserId(StringUtils.toString(object[0]));
				}
				dto.setClients(clients);
				dtoList.add(dto);
			}
		}
		return dtoList;

	}

	@Override
	public Long getAssistUserCntByCondition(AssistUserQueryDto param) {
		String relName = param.getRelName().trim();
		String idCard = param.getIdCard().trim();
		String telephone = param.getTelephone().trim();
		String state = param.getState().trim();
		String isActivation = param.getIsActive();
		Set<UserClientRel> userClientRelList = null;
		User user = UserUtils.getCurrentUser();
		String createId=null;
		createId = user.getId();
//		createId="2253887031349248";
		//创建Set存储manageId，去掉重复数据
		Set manageIdss = null;
		//创建List把Set值插入
		List<String> manageIds = new ArrayList<>();
		String manageId = null;
		//通过创建者ID查询应用和管理范围
		userClientRelList = userClientRelResource.getSetClientRelByUserId(createId);
		if (null != userClientRelList && userClientRelList.size() > 0) {
			manageIdss = new HashSet<>();
			for (UserClientRel ucr : userClientRelList) {
				manageId = ucr.getManageId();
				manageIdss.add(manageId);
			}
		}
		StringBuffer sb = new StringBuffer("SELECT  count( DISTINCT u.id)\n" +
				" FROM\n" +
				"	t_4a_actors u , tf_f_assist_manage t ");
		sb.append(" where   u.id=t.fz_user_id   ");
		manageIds.addAll(manageIdss);
		if (ListUntils.isNotNull(manageIds)) {
			sb.append("  and t.create_manage_id  in :manageIds");
		}
		sb.append(" ");
		if (!StringUtils.isNull(relName)) {
			sb.append(" and u.\"name\" like :name ");
		}
		if (!StringUtils.isNull(telephone)) {
			sb.append(" and u.\"tele_phone\" like :telephone ");
		}
		if (!StringUtils.isNull(state)) {
			sb.append(" and u.is_account_locked = :state ");
		}
		if (!StringUtils.isNull(idCard)) {
			sb.append(" and u.\"id_card\" like :idCard ");
		}
		if (!StringUtils.isNull(isActivation)) {
			sb.append(" and u.is_activation = :isActivation ");
		}
		Query query = businessEntity.createNativeQuery(sb.toString());
		if (!StringUtils.isNull(relName)) {
			query.setParameter("name", "%" + relName + "%");
		}
		if (!StringUtils.isNull(idCard)) {
			query.setParameter("idCard", "%" + idCard + "%");
		}
		if (!StringUtils.isNull(telephone)) {
			query.setParameter("telephone", "%" + telephone + "%");
		}
		//0----禁用  1----启用
		if (!StringUtils.isNull(state)) {
			boolean stateFlag = true;
			if ("1".equals(state)) {
				stateFlag = false;
			}
			if ("0".equals(state)) {
				stateFlag = true;
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
		return StringUtils.toLong(query.getSingleResult());
	}


	public List<AssistClientsQueryResultDto> queryClientsByFzUserId(String fzuserId) throws Exception {
		StringBuffer sql = new StringBuffer("select new com.ido85.party.aaaa.mgmt.dto.assist.AssistClientsQueryResultDto" +
				" (c.clientName,t.manageName) from  AssistManage t ,ClientExpand c where c.clientId=t.clientId and  t.fzuserId =:fzuserId ");
		Query query = businessEntity.createQuery(sql.toString());
		query.setParameter("fzuserId", fzuserId);
		return query.getResultList();
	}


	/**
	 * 对申请的管理员进行审核校验条件(批量)
	 *
	 * @param param
	 * @return
	 */
	@Override
	public List<CheckApplyUserResult> checkApplyUser(ApplyUserIdsDto param) {
		List<CheckApplyUserResult> dtoResult = null;
		CheckApplyUserResult dtoApply=null;
		boolean isPartyFlag = false;
		String hash = null;
		List<ApplyUserBasicResultDto> applyUsers = null;
		List<ApplyUserClientManageDto> dtoList = null;
		List<ApplyUserClientManageDto> dtoListNew = null;
		dtoResult = new ArrayList<>();
		if (null != param) {
			if (ListUntils.isNotNull(param.getItemIds())) {

				gw:for(String item : param.getItemIds()) {
					//查询出这个人的基本信息进行校验
					applyUsers = getApplyUseBasicById(item);
					if (ListUntils.isNull(applyUsers)) {
						dtoApply = new CheckApplyUserResult();
						dtoApply.setFlag("fail");
						dtoApply.setMessage("申请人员不存在应用信息，数据异常");
						dtoApply.setItemId(item);
						dtoResult.add(dtoApply);
						continue gw;
					}
					if (ListUntils.isNotNull(applyUsers)) {
						for (ApplyUserBasicResultDto au : applyUsers) {
							/*对基本信息进行校验*/
							if (!"1".equals(au.getStatus())) {
								dtoApply = new CheckApplyUserResult();
								dtoApply.setFlag("fail");
								dtoApply.setMessage("申请人员状态异常，请联系管理员");
								dtoApply.setItemId(item);
								dtoResult.add(dtoApply);
								continue gw;
							}
							hash = StringUtils.getUserNameIDHash(au.getRelName(), au.getIdCard());
//							boolean isBusinessUser = commonApplication.checkIsBusinessUser(hash);
							//判断是否是辅助安全员
							Long isAssistUser = businessUserResource.checkIsAssistUser(hash);
							if (isAssistUser > 0L) {
								dtoApply = new CheckApplyUserResult();
								dtoApply.setFlag("fail");
								dtoApply.setMessage("此用户已经是安全员,或辅助安全员,或审计管理员，所以您不能再给其开通业务管理员");
								dtoApply.setItemId(item);
								dtoResult.add(dtoApply);
								continue gw;
							}
							//当创建业务管理员时，不能为审计员或者安全员
							boolean isSecOrAu = commonApplication.checkIsSecOrAudi(hash);
							if (isSecOrAu) {
								dtoApply = new CheckApplyUserResult();
								dtoApply.setFlag("fail");
								dtoApply.setMessage("此用户已经是安全员，或辅助安全员,或审计管理员，所以您不能再给其开通业务管理员");
								dtoApply.setItemId(item);
								dtoResult.add(dtoApply);
								continue gw;
							}
							isPartyFlag = this.CheckUserExistFromSimple(hash);
							if (!isPartyFlag) {
								dtoApply = new CheckApplyUserResult();
								dtoApply.setFlag("fail");
								dtoApply.setMessage("业务管理员必须是实名认证的党员");
								dtoApply.setItemId(item);
								dtoResult.add(dtoApply);
								continue gw;
							}
							if (StringUtils.isNull(au.getOuName())) {
								dtoApply = new CheckApplyUserResult();
								dtoApply.setFlag("fail");
								dtoApply.setMessage("节点数据异常");
								dtoApply.setItemId(item);
								dtoResult.add(dtoApply);
								continue gw;
							}

							dtoListNew = applyUserRepository.getApplyUserClient(item);
							if (ListUntils.isNotNull(dtoListNew)) {
								for (ApplyUserClientManageDto dto : dtoListNew) {
									/**
									 * 新增整建制校验
									 */
									if(simpleDataService.checkOrgInWhole(dto.getManageId())){
										dtoApply = new CheckApplyUserResult();
										dtoApply.setFlag("fail");
										dtoApply.setMessage(dto.getManageName()+"正在进行组织关系整建制转接");
										dtoApply.setItemId(item);
										dtoResult.add(dtoApply);
										continue gw;
									}
								}
							}
							//节点校验
							BusinessUser user = businessUserResource.getUserByHash(hash);
							if (null != user) {
								BusinessUserVpnRel rel = businessuserVpnRelResources.getRelByUserId(user.getId());
								String ou = rel.getOu();
								if (StringUtils.isNull(ou)) {
									dtoApply = new CheckApplyUserResult();
									dtoApply.setFlag("fail");
									dtoApply.setMessage("节点数据异常");
									dtoApply.setItemId(item);
									dtoResult.add(dtoApply);
									continue gw;
								}
								if (!ou.equals(au.getOuName())) {
									if(!"node0".equals(au.getOuName())&&!"node0".equals(ou)){
										dtoApply = new CheckApplyUserResult();
										dtoApply.setFlag("fail");
										dtoApply.setMessage("节点数据异常，该人员已经为其他地区管理员");
										dtoApply.setItemId(item);
										dtoResult.add(dtoApply);
										continue gw;
									}
								}

							//查询是否已经存在vpn   如果存在更新userId
							String vpnUserId = null;
							vpnUserId = businessuserVpnRelResources.getVpnByIdCard(au.getIdCard());
							if (StringUtils.isNull(vpnUserId)) {
								dtoApply = new CheckApplyUserResult();
								dtoApply.setFlag("fail");
								dtoApply.setMessage("该账号VPN账号异常");
								dtoApply.setItemId(item);
								dtoResult.add(dtoApply);
								continue gw;
							}

							dtoList = applyUserRepository.getApplyUserClient(item);
							if (ListUntils.isNotNull(dtoList)) {
								for (ApplyUserClientManageDto dto : dtoList) {
									//判断用户是否在该应用下已经存在
									List<ClientUserRel> clientUserRels = clientUserRelResources.getCurByClientUser(dto.getClientId(), user.getId());
									if (ListUntils.isNotNull(clientUserRels)) {
										dtoApply = new CheckApplyUserResult();
										dtoApply.setFlag("fail");
										dtoApply.setMessage("该用户已经有当前应用的管理权限");
										dtoApply.setItemId(item);
										dtoResult.add(dtoApply);
										continue gw;
									}
								}
							}
						}
						}
					}
					dtoApply = new CheckApplyUserResult();
					dtoApply.setFlag("success");
					dtoApply.setMessage("校验成功");
					dtoApply.setItemId(item);
					dtoResult.add(dtoApply);
					continue gw;

			}
			}
		} else {
			dtoApply = new CheckApplyUserResult();
			dtoApply.setFlag("fail");
			dtoApply.setMessage("请求数据错误");
			dtoApply.setItemId(null);
			dtoResult.add(dtoApply);
			return dtoResult;
		}
//		dtoApply=new CheckApplyUserResult();
//		dtoApply.setFlag("fail");
//		dtoApply.setMessage("请求数据错误");
//		dtoApply.setItemId(null);
//		dtoResult.add(dtoApply);
		return dtoResult;
	}

	/**
	 * 通过itemId查询出申请人的基本信息
	 *
	 * @param itemId
	 * @return
	 */
	private List<ApplyUserBasicResultDto> getApplyUseBasicById(String itemId) {
		List<ApplyUserBasicResultDto> applyUser = null;
		ApplyUserBasicResultDto dto = null;
		try {

			StringBuffer sb = new StringBuffer("select ur.idCard, ur.relName, ur.ou, ur.status " +
					"   from ApplyUserBasic  ur  where   ur.delFlag= '0' and  ur.applyUserId = :itemId ");
			Query q = businessEntity.createQuery(sb.toString());
			q.setParameter("itemId", itemId);
			List<Object[]> basicData = q.getResultList();
			if (ListUntils.isNotNull(basicData) && basicData.size() == 1) {
				applyUser = new ArrayList<>();
				dto = new ApplyUserBasicResultDto();
				for (Object[] object : basicData) {
					dto.setIdCard(StringUtils.toString(object[0]));
					dto.setRelName(StringUtils.toString(object[1]));
					dto.setOuName(StringUtils.toString(object[2]));
					dto.setStatus(StringUtils.toString(object[3]));
				}
				applyUser.add(dto);
			}
		} catch (Exception e) {
			log.info("通过itemId查询申请人错误");
			return null;
		}
		return applyUser;
	}


	/**
	 * 申请人最后的审核通过，同时开通账号、vpn
	 *
	 * @param param
	 * @return
	 */
	@Override
	@Transactional
	public Map<String, String> passApplyUser(ApplyUserIdsDto param) {
		//获取审核人的信息
		User currentUser = UserUtils.getCurrentUser();

		BusinessUser user = new BusinessUser();
		Map<String, String> result = new HashMap<String, String>();
		List<BusinessUser> entities = new ArrayList<BusinessUser>();
		StringBuffer message = new StringBuffer();
		ClientUserRel cur = null;
		List<ApplyUserBasic> applyUsers = null;
		List<ApplyUser> dtoList = null;
		Set<String> ouList = new HashSet<>();
		UserRole ur = null;
		List<UserRole> userRoleList = new ArrayList<>();
		List<ClientUserRel> curList = new ArrayList<>();
		BusinessUser u =null;
		if (null != param) {
			if (ListUntils.isNotNull(param.getItemIds())) {
				//查询基本信息（批量）
				applyUsers = applyUserBasicResources.getApplyUseBasicById(param.getItemIds());
				if (ListUntils.isNotNull(applyUsers)) {
					for (ApplyUserBasic au : applyUsers) {
						String hash = StringUtils.getUserNameIDHash(au.getRelName(), au.getIdCard());
						u = businessUserResource.getUserByHash(hash);
						if (null != u) {
							BusinessUserVpnRel rel = businessuserVpnRelResources.getRelByUserId(u.getId());
							String ou = rel.getOu();
							//如果原来账号在node0共享节点，新的账号在节点应用，则将vpn账号移动到节点区
							if ("node0".equals(ou) && !"node0".equals(au.getOu())) {
								ldapApplication.changeVPNOuPwd(ou, rel.getVpn(), au.getOu(), u.getId());
							}

							dtoList = applyUserRepository.getApplyUserClientAndManage(au.getApplyUserId());
							for (ApplyUser dto : dtoList) {
								ur = new UserRole();
								ur.setId(idGenerator.next());
								ur.setManageId(dto.getApplyManageId());
								ur.setManageName(dto.getApplyManageName());
								ur.setManageCode(dto.getApplyManageCode());
								ur.setRoleId(dto.getRoleId());
								ur.setUserId(u.getId());
								userRoleList.add(ur);

								cur = new ClientUserRel();
								cur.setClientId(dto.getClientId());
								cur.setClientUserRelId(idGenerator.next());
								cur.setUserId(u.getId());
								curList.add(cur);
							}
							userRoleResource.save(userRoleList);
							clientUserRelResources.save(curList);
							log.info("辅助安全员" + "此次更新业务管理员账号为======:" + au.getIdCard() + "姓名为:" + au.getRelName());
							//修改applyUserBasic中的状态和通过时间,增加通过时审核人名字
							applyUserBasicResources.updateStatusAndTime(au.getApplyUserId(), new Date(), currentUser.getName());
						} else {
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
							user.setTelePhone(au.getTelephone());
							user.setHash(StringUtils.getUserNameIDHash(au.getRelName(), au.getIdCard()));
							user.setIdCard(au.getIdCard());
							user.setName(au.getRelName());
							user.setCreateBy(currentUser.getId());
							entities.add(user);

							ouList.add(au.getOu());

							dtoList = applyUserRepository.getApplyUserClientAndManage(au.getApplyUserId());
							if (ListUntils.isNotNull(dtoList)) {
								for (ApplyUser dto : dtoList) {
									ur = new UserRole();
									ur.setId(idGenerator.next());
									ur.setManageId(dto.getApplyManageId());
									ur.setManageName(dto.getApplyManageName());
									ur.setManageCode(dto.getApplyManageCode());
									ur.setRoleId(dto.getRoleId());
									ur.setUserId(userId);
									userRoleList.add(ur);

									cur = new ClientUserRel();
									cur.setClientId(dto.getClientId());
									cur.setClientUserRelId(idGenerator.next());
									cur.setUserId(userId);
									curList.add(cur);
								}
							}

							String ouToSVN = null;

							for (String ou : ouList) {
								ouToSVN = ou;
							}
							if (StringUtils.isNull(ouToSVN)) {
								result.put("flag", "fail");
								result.put("message", "节点数据异常,请联系管理员");
								return result;
							}
							//查询是否已经存在vpn   如果存在更新userId
							BusinessUserVpnRel vpnUser = null;
							vpnUser = businessuserVpnRelResources.getVpnUserByIdCard(au.getIdCard());
							if (null == vpnUser) {
//								/**
//								 * 更新vpn关联表userId
//								 */
//								businessuserVpnRelResources.updateVpnByIdCard(userId,au.getIdCard(),ouToSVN);
								boolean vpnFlag = commonApplication.createVPN(userId, code, au.getTelephone(), au.getIdCard(), ouToSVN);
								log.info("辅助安全员" + currentUser.getId() + currentUser.getName() + "创建VPN账号结果=======" + vpnFlag);
							} else {
								String ouName = vpnUser.getOu();
								//如果原来账号在node0共享节点，新的账号在节点应用，则将vpn账号移动到节点区
								if ("node0".equals(ouName) && !"node0".equals(ouToSVN)) {
									ldapApplication.changeVPNOuPwd(ouName, vpnUser.getVpn(), ouToSVN, userId);
								} else if (!"node0".equals(ouToSVN) && !ouName.equals(ouToSVN)) {
									result.put("flag", "fail");
									result.put("message", "您选择的管理范围与原有管理范围不在同一节点");
									return result;
								}
							}
							log.info("辅助安全员" + currentUser.getId() + currentUser.getName() + "此次开通的业务管理员账号为======:" + au.getIdCard() + "姓名为:" + au.getRelName());
							//修改applyUserBasic中的状态和通过时间,增加通过时审核人名字
							applyUserBasicResources.updateStatusAndTime(au.getApplyUserId(), new Date(), currentUser.getName());
							businessUserResource.save(entities);
							userRoleResource.save(userRoleList);
							clientUserRelResources.save(curList);
						}
					}

				}else{
					result.put("flag", "fail");
					result.put("message", "请求参数错误");
					return result;
				}
			}
			result.put("flag", "success");
			result.put("message", "审核通过");
			return result;
		}else{
			result.put("flag", "fail");
			result.put("message", "请求参数错误");
			return result;
		}

	}
	/**
	 * 查询这些用户是否存在
	 */
	public List<String> checkAssistUserExists(@Valid List<CheckBusinessUserParam> param) {
		List<String> result = null;
		String idCard = null;
		String name = null;
		List<String> hashs = null;
		List<BusinessUser> userList = null;
		if(ListUntils.isNotNull(param)){
			userList = new ArrayList<>();
			result = new ArrayList<String>();
			hashs = new ArrayList<String>();
			for(CheckBusinessUserParam p:param){
				idCard = p.getIdCard();
				name = p.getName();
				hashs.add(StringUtils.getUserNameIDHash(name, idCard));
			}
			userList = getUserListByHashs(hashs);
			if(ListUntils.isNotNull(userList)){
				for(BusinessUser u:userList){
					result.add(u.getIdCard()+"|"+u.getName());
				}
			}
		}
		return result;
	}


	private List<BusinessUser> getUserListByHashs(List<String> hashs) {
		StringBuffer sb = new StringBuffer();
		sb.append("select u from BusinessUser u  where  u.enabled ='t'  ");
		if(ListUntils.isNotNull(hashs)){
			sb.append(" and u.hash in :hashs");
		}
		Query q = businessEntity.createQuery(sb.toString(),BusinessUser.class);
		if(ListUntils.isNotNull(hashs)){
			q.setParameter("hashs",hashs);
		}
		List<BusinessUser> userList = q.getResultList();
		return userList;
	}

	/**
	 * 从配置文件中读取各个应用的名称(用于是否是17地市县级)
	 * @param clientsFromPro
	 * @return
	 */
	public List getClientsFromPro(String  clientsFromPro) {
		List<String> clients = new ArrayList<>();
		String[] clientStrs =null;
		if(clientsFromPro.length() >= 0){
			clientStrs=clientsFromPro.split("/");
			if(clientStrs.length>=0){
				for(int i=0;i<clientStrs.length;i++){
					clients.add(clientStrs[i]);
				}
			}
		}
		return clients;
	}

	/**
	 * 导出业务管理员授权码
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public String exportAuthorizationCode(ExportAuthorizationCodeParam param, HttpServletRequest request) throws FileNotFoundException, IOException {
		User currentUser = UserUtils.getCurrentUser();
		List<String> ids = param.getIds();
		List<BusinessUser> users = businessUserResource.getUserByIds(ids);
		ExportCompareExcel detail = new ExportCompareExcel("", ExportAuthorizationCodeVo.class, 1, StringUtils.toString(ids.size()));
		List<ExportAuthorizationCodeVo> voList = getExportAuthorizationCode(ids);
//		excel名字生成
		String fileName = "辅助安全员授权码_" + DateUtils.getDate("yyyyMMddHHmmss") + "_" + StringUtils.getRandomNum(3) + ".xls";
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

}
