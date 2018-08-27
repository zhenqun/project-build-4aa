package com.ido85.party.aaaa.mgmt.security.authentication.application.impl;

import com.ido85.party.aaaa.mgmt.business.application.BusinessUserApplication;
import com.ido85.party.aaaa.mgmt.business.domain.BusinessUser;
import com.ido85.party.aaaa.mgmt.business.domain.BusinessUserVpnRel;
import com.ido85.party.aaaa.mgmt.business.domain.ClientUserRel;
import com.ido85.party.aaaa.mgmt.business.domain.UserRole;
import com.ido85.party.aaaa.mgmt.business.resources.BusinessUserResources;
import com.ido85.party.aaaa.mgmt.business.resources.BusinessuserVpnRelResources;
import com.ido85.party.aaaa.mgmt.business.resources.ClientUserRelResources;
import com.ido85.party.aaaa.mgmt.business.resources.UserRoleResource;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.*;
import com.ido85.party.aaaa.mgmt.dto.expand.AccountSyncParam;
import com.ido85.party.aaaa.mgmt.dto.expand.CheckAccount;
import com.ido85.party.aaaa.mgmt.dto.expand.CheckOrgAccount;
import com.ido85.party.aaaa.mgmt.dto.ldap.AddAccountDto;
import com.ido85.party.aaaa.mgmt.internet.domain.InternetRole;
import com.ido85.party.aaaa.mgmt.internet.domain.InternetUser;
import com.ido85.party.aaaa.mgmt.internet.domain.InternetUserDelLog;
import com.ido85.party.aaaa.mgmt.internet.domain.InternetUserRole;
import com.ido85.party.aaaa.mgmt.internet.dto.ApplicationDto;
import com.ido85.party.aaaa.mgmt.internet.resource.InternetRoleResources;
import com.ido85.party.aaaa.mgmt.internet.resource.InternetUserDelLogResources;
import com.ido85.party.aaaa.mgmt.internet.resource.InternetUserResources;
import com.ido85.party.aaaa.mgmt.internet.resource.InternetUserRoleResources;
import com.ido85.party.aaaa.mgmt.ldap.application.LdapApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.CommonApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.UserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.*;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.*;
import com.ido85.party.aaaa.mgmt.security.common.UserUtils;
import com.ido85.party.aaaa.mgmt.security.utils.DateUtils;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.SmsService;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.*;

//import com.ido85.party.aaaa.mgmt.security.authentication.domain.ClientUserRel;
@Named
public class CommonApplicationImpl implements CommonApplication{

	@Inject
	private ApplicationResource applicationResource;
	
	@Inject
	private MessageLogResources messageLogResources;
	
	@Inject
	private SmTemplateResouces smTemplateResouces;
	
	@Inject
	private SmsService smsService;
	
	@Inject
	private IdGenerator idGenerator;

	@Value("${security.oauth2.server.tokenUrl}")
	private String tokenUrl;
	
	@Inject
	private UserVpnRelResources userVpnRelResources;

	@Inject
	@LoadBalanced
	private RestTemplate restTemplate;

	@Inject
	private UserResources userResources;
	
	@Inject
	private BusinessuserVpnRelResources businessuserVpnRelResources;
	
	@Inject
	private LdapApplication ldapApplication;

	@Inject
	private BusinessUserResources businessUserResources;

	@Inject
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@Inject
	private ResetVpnLogResources resetVpnLogResources;


	@Inject
	private BusinessUserApplication businessUserApplication;

	@Inject
	private UserApplication userApplication;

	@Inject
	private ClientUserRelResources clientUserRelResources;

	@Inject
	private UserRoleResource userRoleResource;

	@Inject
	private PasswordEncoder encoder;

	@Value("${smUrl}")
	private String smUrl;
	@Inject
	private InternetUserResources internetUserResources;

	@Inject
	private InternetUserRoleResources internetUserRoleResources;

	@Inject
	private InternetRoleResources internetRoleResources;

	@Inject
	private InternetUserDelLogResources internetUserDelLogResources;

	/**
	 * 查询所有应用
	 * @return
	 */
	public List<ApplicationDto> applicationQuery() {
	    List<Application> applications	= applicationResource.getAllApplication();
	    List<ApplicationDto> dtoList = null;
	    if(ListUntils.isNotNull(applications)){
	    	dtoList = new ArrayList<ApplicationDto>();
	    	for(Application application:applications){
	    		ApplicationDto dto = new ApplicationDto();
	    		dto.setApplicationId(application.getApplicationId());
	    		dto.setApplicationName(application.getApplicationName());
	    		dtoList.add(dto);
	    	}
	    }
		return dtoList;
	}

	/**
	 * 校验手机验证码
	 */
	public Map<String, Object> isVerifyCodeValid(String telephone, String type,String veifyCode) {
		Map<String, Object> map = new HashMap<String,Object>();
		MessageLog messageLog = null;
		List<MessageLog> list = messageLogResources.getVerifyCodeByTelephoneandType(telephone,type);
		if(ListUntils.isNotNull(list)){
			messageLog = list.get(0);
		}else{
			map.put("flag", "fail");
			map.put("message", "没有手机验证码记录！");
			return map;
		}
		Date expireDate = messageLog.getExpireDate();
		Date now = new Date();
		Long minutes = DateUtils.difference(now,expireDate);
		if(minutes > 0){
			map.put("flag", "fail");
			map.put("message", "验证码已失效，请重新获取!");
			return map;
		}
		String rightCode = messageLog.getVerifycode();
		if(!rightCode.equals(veifyCode)){
			map.put("flag", "fail");
			map.put("message", "验证码错误!");
			return map;
		}
		map.put("flag", "success");
		map.put("message", "验证码正确!");
		return map;
	}

	@Override
	public boolean createVPN(String userId,String code,String phone,String idCard,String ou) {
		AddAccountDto dto = null;
		BusinessUserVpnRel rel = null;
		dto = new AddAccountDto();
		dto.setIdCard(idCard);
		if(StringUtils.isNull(ou)){
			dto.setOu("node0");
		}else{
			dto.setOu(ou);
		}
		dto.setPassword(null);
		boolean flag = ldapApplication.createVPN(dto);
		if(flag){
			rel = new BusinessUserVpnRel();
			rel.setVpn(idCard);
			rel.setCreateDate(new Date());
			rel.setOu(ou);
			rel.setUserId(userId);
			rel.setUserVpnId(idGenerator.next());
			rel.setFlag("1");
			businessuserVpnRelResources.save(rel);
			sendMessage("2006",code, phone);
		}else{
			rel = new BusinessUserVpnRel();
			rel.setVpn(idCard);
			rel.setCreateDate(new Date());
			rel.setOu(ou);
			rel.setUserId(userId);
			rel.setUserVpnId(idGenerator.next());
			rel.setFlag("0");
			businessuserVpnRelResources.save(rel);
		}
		return flag;
	}

	/**
	 * 发送授权码以及VPN账号
	 */
	private void sendMessage(String type, String authorizationCode, String telephone) {
		Map<String,Object> map = new HashMap<String,Object>();
		MessageLog messageLog = new MessageLog();
		//查模板
		SmTemplate smTemplate = smTemplateResouces.getTemplateByType(type);
		StringBuffer messageContent = null;
		if(null != smTemplate){
			messageContent = new StringBuffer();
			messageContent.append(smTemplate.getSmTemplateOrgname()+" "+
					smTemplate.getSmTemplateName()+smTemplate.getSmTemplateContent());
		}
		String content = String.format(messageContent.toString(), authorizationCode);
		SendMessageDto result = null;
		//设置messageLog值
		messageLog.setMessageContent(content);
		messageLog.setCreateDate(new Date());
		messageLog.setMessageType(type);
		messageLog.setVerifycode("000000");
		messageLog.setVerifycodeId(idGenerator.next());
		messageLog.setTelephone(telephone);
		//发送短信
		try {
			result = smsService.sendverificationCode(telephone,content);
			if(null != result){
				messageLog.setDetail(result.getMessage());
				String status = result.getReturnstatus();
				if("Success".equals(status)){
					messageLog.setSuccess(true);
					map.put("flag", "success");
					map.put("message", "发送成功!");
				}else if("Faild".equals(status)){
					messageLog.setSuccess(false);
					map.put("flag", "fail");
					map.put("message", "发送失败!");
				}
			}
		}catch (Exception e){
			messageLog.setDetail(e.getMessage());
			messageLog.setSuccess(false);
		}finally {
			messageLogResources.save(messageLog);
		}
	}

	/**
	 * 获取token
	 * @return
	 */
	private String getToken() {
//		System.setProperty("javax.net.ssl.trustStore", "D:/partyworkspace/party-build-4a/party-build-4a-sso-vpn/src/main/resources/sso-vpn.jks"); 
//		System.setProperty("javax.net.ssl.trustStoreType", "JKS"); 
//		System.setProperty("javax.net.ssl.trustStorePassword", "123456"); 
//		System.setProperty("org.jboss.security.ignoreHttpsHost", "true");
//		System.setProperty("javax.net.ssl.trustAnchors", "d:\\test_store");
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization","Basic cGFydHktYnVpbGQtNGEtc3luYzppZG84NVImRGNlbnRlcg==");
		HttpEntity<String> formEntity = new HttpEntity<>("", headers);
//		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//		params.add("grant_type", "client_credentials");
		Map<String,String> o = restTemplate.exchange(tokenUrl, HttpMethod.POST,formEntity,Map.class).getBody();
		if(null != o){
			return o.get("access_token").toString();
		}else{
			return null;
		}
	}

	//检测安全员审计员所选管理范围是否合法
	public boolean checkSecurityUserOu(String id, String ou) {
		String ouName = userVpnRelResources.getOuNameByUserId(id);
//		ou = "People";
		if(StringUtils.isNull(ouName) || StringUtils.isNull(ou)){
			return false;
		}
		if(ou.equals(ouName)){
			return true;
		}
		return false;
	}

	/**
	 * 修改vpn密码
	 * 
	 */
	public boolean updateVpnPwd(String vpn, String ouName, String vpnpwd) {
		return ldapApplication.updateVpnPwd(vpn,ouName,vpnpwd);
	}

	@Override
	public boolean createVPNForSecurity(String userId, String code, String telephone, String idCard, String ouName,String type) {
		AddAccountDto dto = null;
		UserVpnRel rel = null;
		dto = new AddAccountDto();
		dto.setIdCard(idCard);
		dto.setOu(ouName); 
		dto.setPassword(null);
		boolean flag = ldapApplication.createVPN(dto);
		if(flag){
			rel = new UserVpnRel();
			rel.setVpn(idCard);
			rel.setUserId(userId);
			rel.setOuName(ouName);
			rel.setUserOuId(idGenerator.next());
			rel.setFlag("1");
			userVpnRelResources.save(rel);
			sendMessage(type,code, telephone);
		}else{
			rel = new UserVpnRel();
			rel.setVpn(idCard);
			rel.setUserId(userId);
			rel.setOuName(ouName);
			rel.setUserOuId(idGenerator.next());
			rel.setFlag("0");
			userVpnRelResources.save(rel);
		}
		return flag;
	}

	/**
	 * 判断用户是否为业务管理员
	 * @param hash
	 * @return
	 */
	public boolean checkIsBusinessUser(String hash) {
		BusinessUser businessUser = businessUserResources.getUserByHash(hash);
		if(null == businessUser){
			return false;
		}
		return true;
	}

	@Override
	public boolean checkBusinessOu(String userId, String ouName) {
		BusinessUserVpnRel rel = businessuserVpnRelResources.getRelByUserId(userId);
		String ou = rel.getOu();
//		ou = "People";
		if(StringUtils.isNull(ouName) || StringUtils.isNull(ou)){
			return false;
		}
		if(ou.equals(ouName)){
			return true;
		}
		//如果原来账号在node0共享节点，新的账号在节点应用，则将vpn账号移动到节点区
		if("node0".equals(ou) && !"node0".equals(ouName)){
			ldapApplication.changeVPNOuPwd(ouName,rel.getVpn(),ou,userId);
			return true;
		}else if(!"node0".equals(ouName) && !ouName.equals(ouName)){
			return false;
		}else if("node0".equals(ouName)){
			return true;
		}
		return false;
	}

	/**
	 * 判断用户是否为安全员或者审计员
	 * @param hash
	 * @return
	 */
	public boolean checkIsSecOrAudi(String hash) {
		User u = userResources.getUserByHash(hash);
		if(null == u){
			return false;
		}
		return true;
	}

	/**
	 * 重置vpn密码
	 * @param param
	 * @return
     */
	public boolean resetVpnPassword(ResetVpnPassword param) {
		String userId = param.getUserId();
		String type = param.getType();
		String ou = null;
		String uid = null;
		String createBy = null;
		boolean successFlag = false;
		User u = UserUtils.getCurrentUser();
		if(null != u){
			createBy = u .getId();
		}
		if(type.equals("1") || type.equals("2")){
			UserVpnRel rel = userVpnRelResources.getRelByUserId(userId);
			if(null != rel){
				ou = rel.getOuName();
				uid = rel.getVpn();
				successFlag = ldapApplication.resetVpnPassword(ou,uid);
			}else{
				return false;
			}
		}
		if(type.equals("3")){
			BusinessUserVpnRel rel = businessuserVpnRelResources.getRelByUserId(userId);
			if(null != rel){
				ou = rel.getOu();
				uid = rel.getVpn();
				successFlag = ldapApplication.resetVpnPassword(ou,uid);
			}else{
				return false;
			}
		}
		if(successFlag){
			//记录重置vpn密码
			final String finalCreateBy = createBy;
			final String finalUid = uid;
			final String finalOu = ou;
			threadPoolTaskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					new Thread(()->{
						ResetVpnLog log = new ResetVpnLog();
						log.setCreateBy(finalCreateBy);
						log.setVpn(finalUid);
						log.setOu(finalOu);
						log.setId(idGenerator.next());
						log.setCreateDate(new Date());
						log.setType(type);
						log.setUserId(userId);
						resetVpnLogResources.save(log);
					}).run();

				}
			});
			return true;
		}
		return false;
	}

	@PersistenceContext(unitName = "admin")
	private EntityManager adminEntity;

	@PersistenceContext(unitName = "business")
	private EntityManager businessEntity;
	/**
	 * 查询组织下是否存在账号
	 * @param param
	 * @return
	 */
	public boolean checkOrgAccount(CheckOrgAccount param) {
		String orgId = param.getOrgId();
		if(StringUtils.isNull(orgId)){
			return false;
		}
		//查询是否存在安全员或者审计员
		StringBuffer sb = new StringBuffer("SELECT DISTINCT\n" +
				"	count(A .\"id\")\n" +
				"FROM\n" +
				"	t_4a_actors A,\n" +
				"	r_4a_user_client C\n" +
				"WHERE\n" +
				"	A . ID = C .user_id\n" +
				"AND C .manage_id = :orgId");
		Query q = adminEntity.createNativeQuery(sb.toString());
		q.setParameter("orgId",orgId);
		Long cnt = StringUtils.toLong(q.getSingleResult());
		if(cnt.longValue() >= 1L){
			return false;
		}
		//查询是否存在管理员
		StringBuffer sb2 = new StringBuffer("SELECT\n" +
				"	COUNT (A . ID)\n" +
				"FROM\n" +
				"	t_4a_actors A,\n" +
				"	r_4a_user_role r\n" +
				"WHERE\n" +
				"	A .\"id\" = r.user_id\n" +
				"AND r.manage_id = :orgId");
		Query q2 = businessEntity.createNativeQuery(sb2.toString());
		q2.setParameter("orgId",orgId);
		Long cnt2 = StringUtils.toLong(q2.getSingleResult());
		if(cnt2.longValue() >= 1L){
			return false;
		}
		return true;
	}

	/**
	 * 账号同步  内容管理系统使用
	 */
	public Map<String, String> accountSync(AccountSyncParam param) {
		Map<String, String> map = new HashMap<>();
		String relName = param.getRelName();
		String username = param.getUsername().toUpperCase();
		String pwd = param.getPassword();
		String telephone = param.getTelephone();
		String vpn = param.getVpn();
		String clientId = param.getClientId();
		Long roleId = param.getRoleId();
		//生成hash，看是否已经存在该用户
		String hash = StringUtils.getUserNameIDHash(relName,username);
		//判断其是否为安全员或者审计员
		User u = userApplication.getUserByHash(hash);
		if(null != u){
			map.put("flag","fail");
			map.put("message","该用户已经为应用安全员或者审计员！");
			return map;
		}
		//判断改用户是否存在
		BusinessUser user = businessUserApplication.getBusinessUserByHash(hash);
		if(null == user){
			user = new BusinessUser();
			//添加该用户
			String userId = StringUtils.toString(idGenerator.next());
			user.setId(userId);
			user.setAccountExpired(false);
			user.setAccountLocked(false);
			user.setPwdExpired(false);
			user.setActivation(true);
			user.setCreateDate(new Date());
			user.setDisabled(false);
			user.setEnabled(true);
			user.setTelePhone(telephone);
			user.setHash(hash);
			user.setIdCard(null);
			user.setName(relName);
			user.setUsername(username);
			user.setActivationDate(new Date());
			user.setPassword(encoder.encode(pwd));
			businessUserResources.save(user);
			//添加用户应用关联
			ClientUserRel cur = new ClientUserRel();
			cur.setClientId(clientId);
			cur.setClientUserRelId(idGenerator.next());
			cur.setUserId(userId);
			clientUserRelResources.save(cur);
			//添加vpn用户关联
			AddAccountDto dto = null;
			BusinessUserVpnRel rel = null;
			dto = new AddAccountDto();
			dto.setIdCard(username);
			dto.setOu("People");
			dto.setPassword(pwd);
			boolean flag = ldapApplication.createVPN(dto);
			if(flag){
				rel = new BusinessUserVpnRel();
				rel.setVpn(username);
				rel.setCreateDate(new Date());
				rel.setOu("People");
				rel.setUserId(userId);
				rel.setUserVpnId(idGenerator.next());
				rel.setFlag("1");
				businessuserVpnRelResources.save(rel);
			}else{
				rel = new BusinessUserVpnRel();
				rel.setVpn(username);
				rel.setCreateDate(new Date());
				rel.setOu("People");
				rel.setUserId(userId);
				rel.setUserVpnId(idGenerator.next());
				rel.setFlag("0");
				businessuserVpnRelResources.save(rel);
			}
			//不添加角色管理范围，该用户为内容管理系统自己判断其角色范围
		}else{
			//判断该用户是不是拥有供稿员角色
			UserRole ur = userRoleResource.getRoleByUserIdRoleId(user.getId(),roleId);
			if(null != ur){
				map.put("flag","fail");
				map.put("message","该用户已经拥有该角色!");
				return map;
			}
			//判断该用户是否已经有改应用的管理范围,如果有那么返回已存在，如果没有则新增一条关联
			List<ClientUserRel> clientUserRels = clientUserRelResources.getCurByClientUser(clientId,u.getId());
			if(ListUntils.isNotNull(clientUserRels)){
				map.put("flag", "fail");
				map.put("message", "该用户已经有当前应用的管理权限");
				return map;
			}
			ClientUserRel cur =  new ClientUserRel();
			cur.setClientId(clientId);
			cur.setClientUserRelId(idGenerator.next());
			cur.setUserId(u.getId());
			clientUserRelResources.save(cur);
		}
		map.put("flag","success");
		map.put("message","添加成功!");
		return map;
	}

	/**
	 * 查询hashs下是否为三员
	 * @param hashs
	 * @return
	 */
	public List<String> checkHashAdmin(List<String> hashs) {
		List<String> result = new ArrayList<>();
		if(ListUntils.isNotNull(hashs)){
			//查询是不是管理员
			List<BusinessUser> bus = businessUserApplication.checkHashAdmin(hashs);
			//查询是不是管理员
			List<User> users = userApplication.checkHashAdmin(hashs);
			for(BusinessUser bu:bus){
				result.add(bu.getHash());
			}
			for(User u:users){
				result.add(u.getHash());
			}
			//去重
			HashSet h = new HashSet(result);
			result.clear();
			result.addAll(h);
		}
		return result;
	}

	/**
	 * 校验验证码  调用短信服务
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> isSSOVerifyCodeValid(String telephone, String type,String veifyCode){
		RestTemplate restTemplate = new RestTemplate();
		VerifyCodeParam param = new VerifyCodeParam();
		param.setTelephone(telephone);
		param.setType(type);
		param.setVeifyCode(veifyCode);
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(mediaType);
//		headers.add("Authorization","bearer "+tokenServie.getToken());
		HttpEntity<VerifyCodeParam> formEntity = new HttpEntity<>(param, headers);
		return (Map<String, Object>)restTemplate.postForEntity(smUrl+"/isVerifyCodeValid", formEntity, Map.class).getBody();
	}

	/**
	 * 检测该账号是否可以进行删除（党员信息整改）
	 * @param checkAccount
	 * @return
	 */
	@Override
	public Map<String, String> checkAccount(CheckAccount checkAccount) {
		Map<String,String> map = new HashMap<String,String>();
		String hash = StringUtils.getUserNameIDHash(checkAccount.getName(),checkAccount.getIdCard());
		InternetUser user = internetUserResources.getInterUserByHash(hash);
		if(null == user){
			map.put("flag","false");
			map.put("message","用户不存在");
			return map;
		}
		//检测手机号是否正确
		if(!checkAccount.getTelphone().equals(user.getTelePhone())){
			map.put("flag","false");
			map.put("message","用户手机号错误,正确手机号为:"+user.getTelePhone());
			return map;
		}
		//检测是否是安全员或者审计员
		User actors = userResources.getUserByHash(hash);
		//检测是否是管理员
		if(null != actors){
			map.put("flag","false");
			map.put("message","用户为安全员或者审计员");
			return map;
		}
		BusinessUser bu = businessUserResources.getUserByHash(hash);
		if(null != bu){
			map.put("flag","false");
			map.put("message","用户为管理员");
			return map;
		}
		//检测是否为第一书记等角色
//		Set<InternetRole> roles = user.getRoles();
		List<InternetUserRole> roles = internetUserRoleResources.getRolesByUserId(user.getId());
		if(null != roles && roles.size() > 0){
			for(InternetUserRole r:roles){
				if("0".equals(r.getDelFlag()) && r.getRoleId() != 1L){
					InternetRole role = internetRoleResources.getRoleById(r.getRoleId());
					if(null != role){
						map.put("flag","false");
						map.put("message","用户为:"+role.getDescription());
						return map;
					}
				}
			}
		}
		map.put("flag","true");
		map.put("message","可以删除");
		return map;
	}


	/**
	 * 删除账号（党员信息整改）
	 * @param checkAccount
	 * @return
	 */
	@Override
	@Transactional
	public boolean delAccount(CheckAccount checkAccount) {
		String hash = StringUtils.getUserNameIDHash(checkAccount.getName(),checkAccount.getIdCard());
		InternetUser user = internetUserResources.getInterUserByHash(hash);
		if(null != user){
			try{
				//删除党员账号
				internetUserResources.delete(user);
				//生成日志
				InternetUserDelLog internetUserDelLog = new InternetUserDelLog();
				internetUserDelLog.setCreateDate(user.getCreateDate());
				internetUserDelLog.setHash(user.getHash());
				internetUserDelLog.setId(user.getId());
				internetUserDelLog.setIdCardHash(user.getIdCardHash());
				internetUserDelLog.setName(user.getName());
				internetUserDelLog.setPassword(user.getPassword());
				internetUserDelLog.setTelePhone(user.getTelePhone());
				internetUserDelLog.setUsername(user.getUsername());
				internetUserDelLog.setUpdateDate(user.getUpdateDate());
				internetUserDelLog.setDelDate(new Date());
				internetUserDelLogResources.save(internetUserDelLog);
				//删除党员角色
				List<InternetUserRole> roles = internetUserRoleResources.getRolesByUserId(user.getId());
				if(ListUntils.isNotNull(roles)){
					for(InternetUserRole role:roles){
						role.setDelFlag("1");
						role.setDelDate(new Date());
					}
					internetUserRoleResources.save(roles);
				}
			}catch (Exception e){
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean checkOrgTypeAccount(CheckOrgAccount param) {
		String orgId=null;
		if(null !=param){
			 orgId= param.getOrgId();
			if(StringUtils.isNull(orgId)){
				return false;
			}
		}

		//查询是否存在安全员或者审计员
		StringBuffer sb = new StringBuffer("SELECT \n" +
				"	count(DISTINCT A .\"id\")\n" +
				"FROM\n" +
				"	t_4a_actors A,\n" +
				"	r_4a_user_client C\n" +
				"WHERE\n" +
				"	A . ID = C .user_id\n" +
				"AND (C .manage_id = :orgId and c.client_id='party-org-info-mgmt-ui') or (c.client_id='party-people-info-mgmt-ui' and C .manage_id = :orgId) ");
		Query q = adminEntity.createNativeQuery(sb.toString());
		q.setParameter("orgId",orgId);
		Long cnt = StringUtils.toLong(q.getSingleResult());
		if(cnt.longValue() >= 1L){
			return false;
		}
		//查询是否存在管理员
		StringBuffer sb2 = new StringBuffer("SELECT\n" +
				"\t count(DISTINCT u.id)\n" +
				"FROM\n" +
				"\tt_4a_actors u\n" +
				"\tLEFT JOIN r_4a_user_role ur ON u.\"id\" = ur.user_id\n" +
				"\tLEFT JOIN r_4a_client_user_rel cur ON u.id = cur.user_id \n" +
				"WHERE\n" +
				" ( cur.client_id='party-org-info-mgmt-ui' \n" +
				" and ur.manage_id= :orgId) or ( cur.client_id='party-people-info-mgmt-ui' and ur.manage_id= :orgId ) ");
		Query q2 = businessEntity.createNativeQuery(sb2.toString());
		q2.setParameter("orgId",orgId);
		Long cnt2 = StringUtils.toLong(q2.getSingleResult());
		if(cnt2.longValue() >= 1L){
			return false;
		}
		return true;
	}
}
