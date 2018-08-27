package com.ido85.party.aaaa.mgmt.security.authentication.application.impl;

import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.AdminDto;
import com.ido85.party.aaaa.mgmt.dto.AdminQueryParam;
import com.ido85.party.aaaa.mgmt.dto.AllotApplicationDto;
import com.ido85.party.aaaa.mgmt.dto.ApplicationAllotParam;
import com.ido85.party.aaaa.mgmt.dto.userinfo.ModifyPasswordParam;
import com.ido85.party.aaaa.mgmt.internet.dto.ApplicationDto;
import com.ido85.party.aaaa.mgmt.security.authentication.application.CommonApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.UserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.*;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.*;
import com.ido85.party.aaaa.mgmt.security.common.UserUtils;
import com.ido85.party.aaaa.mgmt.security.constants.SendMessageConstants;
import com.ido85.party.aaaa.mgmt.security.utils.DateUtils;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Named
public class UserApplicationImpl implements UserApplication {
	@Inject
	private UserResources userResource;
	@Inject
	private AppUserRelResource appuserRelResource;
	@PersistenceContext(unitName = "admin")
	private EntityManager entity;
	@PersistenceContext(unitName = "business")
	private EntityManager businessEntity;
	@Inject
	private IdGenerator idGenerator;
	@Inject
	private RoleResources roleResources;
	@Inject
	private ApplicationResource applicationResource;
	@Inject
	private PasswordInfoResources passwordInfoResources;
	@Inject
	private UserClientRelResource userClientRelResource;
	@Inject
	private CommonApplication commonApplication;
	@Inject
	private PasswordEncoder encoder;

	@Override
	public User loadUserByUserEmail(String email) {
		return userResource.getUserByUserEmail(email);
	}
	@Override
	public int changePassword(Long id, String password) {
		return userResource.changePassword(password, id);
	}
	@Override
	public void  insertUser(User user) {
		 userResource.save(user);

	}
	@Override
	public List<AdminDto> getAdminByCondition(AdminQueryParam param) {
		List<Long> applicationIds = param.getApplicationIds();
		String telephone = param.getTelephone();
		String orgId = param.getOrgId();
		String status = param.getStatus();
		String username = param.getUsername();
		String idCard = param.getIdCard();
		Integer pageNo = param.getPageNo();
		Integer pageSize = param.getPageSize();
		String identity = param.getIdentity();
		StringBuffer sql = new StringBuffer("SELECT DISTINCT\n" +
				"	u.ID,\n" +
				"	u. NAME,\n" +
				"	u.DISABLED,\n" +
				"	u.TELE_PHONE,\n" +
				"	u.EMAIL,\n" +
				"	GROUP_CONCAT(a.application_name),\n" +
				"	u.org_id,\n" +
				"	u.identity,\n" +
				"	u.create_date,\n" +
				"	u.id_card,\n" +
				"	GROUP_CONCAT(a.application_id)\n" +
				"FROM\n" +
				"	t_4a_actors u\n" +
				"LEFT JOIN r_4a_application_user_rel l ON u.ID = l.user_id\n" +
				"LEFT JOIN t_4a_application a ON l.application_id = a.application_id\n" +
				" where 1=1 ");
		if(StringUtils.isNotBlank(username)){
			sql.append("AND u.EMAIL LIKE :email\n");
		}
		if(StringUtils.isNotBlank(status)){
			sql.append("AND u.DISABLED = :flag\n");
		}
		if(StringUtils.isNotBlank(telephone)){
			sql.append("AND u.TELE_PHONE LIKE :telephone\n");
		}
		if(ListUntils.isNotNull(applicationIds)){
			sql.append("AND l.application_id IN :applicationIds ");
		}
		if(StringUtils.isNotBlank(orgId)){
			sql.append("AND u.org_id = :orgId\n");
		}
		if(StringUtils.isNotBlank(idCard)){
			sql.append("AND u.id_card = :idCard\n");
		}
		if(StringUtils.isNotBlank(identity)){
			sql.append("AND u.identity = :identity\n");
		}
		sql.append(" GROUP BY u.ID ");
		Query query = entity.createNativeQuery(sql.toString());
		if(StringUtils.isNotBlank(username)){
			query.setParameter("email", "%"+username+"%");
		}
		if(StringUtils.isNotBlank(status)){
			query.setParameter("flag", status);
		}
		if(StringUtils.isNotBlank(telephone)){
			query.setParameter("telephone", "%"+telephone+"%");
		}
		if(ListUntils.isNotNull(applicationIds)){
			query.setParameter("applicationIds", applicationIds);
		}
		if(StringUtils.isNotBlank(orgId)){
			query.setParameter("orgId", orgId);
		}
		if(StringUtils.isNotBlank(idCard)){
			query.setParameter("idCard", idCard);
		}
		if(StringUtils.isNotBlank(identity)){
			query.setParameter("identity", identity);
		}
		query.setFirstResult((pageNo - 1) * pageSize);
		query.setMaxResults(pageSize);
		List<AdminDto> dtoList = new ArrayList<AdminDto>();
		List<Object[]> objectList = query.getResultList();
		AdminDto dto = null;
		if(null != objectList && objectList.size()>0){
			for(Object[] object:objectList){
				dto = new AdminDto();
				dto.setApplicationNames(StringUtils.toString(object[5]));
				dto.setIdCard(StringUtils.toString(object[9]));
				dto.setName(StringUtils.toString(object[1]));
				dto.setPhone(StringUtils.toString(object[3]));
				dto.setStatus(StringUtils.toString(object[2]).equals("true")?1:0);
				dto.setUserId(StringUtils.toLong(object[0]));
				dto.setUsername(StringUtils.toString(object[4]));
				dto.setIdentity(StringUtils.toString(object[7]));
				dto.setRegisterDate(StringUtils.toString(object[8]));
				//查询机构
				if(!StringUtils.isNull(StringUtils.toString(object[6]))){
					String orgSql = "select o.org_name from tf_f_org o where o.id = :orgId";
					Query orgQuery = businessEntity.createNativeQuery(orgSql);
					orgQuery.setParameter("orgId", StringUtils.toString(object[6]));
					List<Object[]> orgList = orgQuery.getResultList();
					if(null != orgList && orgList.size()>0){
						dto.setOrgName(StringUtils.toString(orgList.get(0)));
					}
				}
				dto.setLastLoginDate(null);
				dto.setApplicationIds(StringUtils.toString(object[10]));
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	@Override
	public boolean modifyAdminStatus(Long userId,Integer status) {
		Boolean s = null;
		if(status==1){
			s = true;
		}
		if(status==0){
			s = false;
		}
		int flag = userResource.modifyAdminStatus(userId,s);
		if(flag == 1){
			return true;
		}
		return false;
	}

    /**
     * 系统管理员分配
     */
	public boolean applicationAllot(ApplicationAllotParam param) {
		List<AllotApplicationDto> appList = param.getApplications();
		Long userId = param.getUserId();
		List<ApplicationUserRel> entities = new ArrayList<ApplicationUserRel>();
		User admin = userResource.findOne(StringUtils.toString(userId));
		//删除分配关系
		List<ApplicationUserRel> rels = appuserRelResource.getAllRelByUserId(userId);
		appuserRelResource.delete(rels);
		//增加分配关系
		Set<Role> roles = new HashSet<Role>();
 		if(ListUntils.isNotNull(appList)){
 			ApplicationUserRel rel = null;
			for(AllotApplicationDto dto:appList){
				rel = new ApplicationUserRel();
				rel.setApplicationId(dto.getApplicationId());
				rel.setApplicationUserId(idGenerator.next());
				rel.setUserId(userId);
				entities.add(rel);

			}
			//增加管理员角色
			Role role = roleResources.getRoleByName("ROLE_ADMIN");
			if(null != role){
				roles.add(role);
			}
			appuserRelResource.save(entities);
			admin.setRoles(roles);
		}
		userResource.save(admin);
		return true;
	}
	@Override
	public Long getAdminCntByCondition(AdminQueryParam param) {
		List<Long> applicationIds = param.getApplicationIds();
		String telephone = param.getTelephone();
		String orgId = param.getOrgId();
		String status = param.getStatus();
		String username = param.getUsername();
		String idCard = param.getIdCard();
		String identity = param.getIdentity();
		StringBuffer sql = new StringBuffer("SELECT count(DISTINCT\n" +
				"	u.ID)\n" +
				"FROM\n" +
				"	t_4a_actors u\n" +
				"LEFT JOIN r_4a_application_user_rel l ON u.ID = l.user_id\n" +
				"LEFT JOIN t_4a_application a ON l.application_id = a.application_id\n" +
				" where 1=1 ");
		if(StringUtils.isNotBlank(username)){
			sql.append("AND u.EMAIL LIKE :email\n");
		}
		if(StringUtils.isNotBlank(status)){
			sql.append("AND u.DISABLED = :flag\n");
		}
		if(StringUtils.isNotBlank(telephone)){
			sql.append("AND u.TELE_PHONE LIKE :telephone\n");
		}
		if(ListUntils.isNotNull(applicationIds)){
			sql.append("AND l.application_id IN :applicationIds ");
		}
		if(StringUtils.isNotBlank(orgId)){
			sql.append("AND u.org_id = :orgId\n");
		}
		if(StringUtils.isNotBlank(idCard)){
			sql.append("AND u.id_card = :idCard\n");
		}
		if(StringUtils.isNotBlank(identity)){
			sql.append("AND u.identity = :identity\n");
		}
		Query query = entity.createNativeQuery(sql.toString());
		if(StringUtils.isNotBlank(username)){
			query.setParameter("email", "%"+username+"%");
		}
		if(StringUtils.isNotBlank(status)){
			query.setParameter("flag", status);
		}
		if(StringUtils.isNotBlank(telephone)){
			query.setParameter("telephone", "%"+telephone+"%");
		}
		if(ListUntils.isNotNull(applicationIds)){
			query.setParameter("applicationIds", applicationIds);
		}
		if(StringUtils.isNotBlank(orgId)){
			query.setParameter("orgId", orgId);
		}
		if(StringUtils.isNotBlank(idCard)){
			query.setParameter("idCard", idCard);
		}
		if(StringUtils.isNotBlank(identity)){
			query.setParameter("identity", identity);
		}
		return StringUtils.toLong(query.getSingleResult());
	}

	@Override
	public void updateLastLoginDate(String name) {
		userResource.updateLashLoginDate(name,new Date());
	}
	@Override
	public User getAdminById(Long userId) {
		return userResource.getOne(StringUtils.toString(userId));
	}
	@Override
	public void saveAdmin(User user) {
		userResource.save(user);
	}
	/**
	 * 查询管理所有应用
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

	@Override
	public User getUserByHash(String hash) {
		return userResource.getUserByHash(hash);
	}
	@Override
	public User getUserByUserName(String username) {
		return userResource.getUserByAccount(username);
	}

	@Override
	public User getUserByIdcardAndName(String idCard) {
		return userResource.getUserByIdcardAndName(idCard);
	}
	@Override
	public Map<String, String> getUserByIds(Set<String> userIds) {
		Map<String, String> map = null;
		List<User> userList = userResource.getUserByIds(userIds);
		if(ListUntils.isNotNull(userList)){
			map = new HashMap<>();
			for(User u:userList){
				map.put(u.getId(), u.getName());
			}
		}
		return map;
	}
	/**
	 * 检测密码是否过期
	 */
	public Long checkUserPwd(String username) {
		User u = userResource.getUserByIdcardTelAccount(username);
		PasswordInfo pi = null;
		Long day = 0L;
		if(null != u){
			List<PasswordInfo> piList = passwordInfoResources.getLastInfoByUserId(u.getId());
			if(ListUntils.isNotNull(piList)){
				pi = piList.get(0);
				Date date = pi.getDate();
				day = DateUtils.pastDays(date);
			}
		}
		return day;
	}

	/**
	 * 获取当前登录用户的ou
	 */
	public String getCurrentUserOu() {
		User u = UserUtils.getCurrentUser();
		String currentUserId = null;
		String ou = null;
		String manageId = null;
		if(u != null){
			currentUserId = u.getId();
			ou = null;

			//根据manageId获取ou
//			if(null != securityrels){
//				manageId = securityrels.getManageId();
//			}
			ou = null;
		}
		return ou;
	}

	/**
	 * 修改密码
	 * @param param
	 * @return
     */
	public Map<String, Object> modifyPassword(ModifyPasswordParam param) {
		String password = param.getPassword();
		String verifyCode = param.getVerifyCode();
		String telephone = param.getTelephone();
		Map<String,Object> result = new HashMap<>();
		boolean checkFlag=false;
		checkFlag = StringUtils.validatePassword(password);
		if (!checkFlag) {
			result.put("flag", "fail");
			result.put("message", "密码强度较弱，请混合使用大小写字母和数字，避开常用密码");
			return result;
		}
//		Zxcvbn zxcvbn = new Zxcvbn();
//		Strength strength = zxcvbn.measure(password);
//		if(null != strength){
//			int score = strength.getScore();
//			if(score<2){
//				result.put("flag", "fail");
//				result.put("message", "密码强度较弱，请混合使用字母和数字，避开常用密码!");
//				return result;
//			}
//		}
		//检测验证码是否正确
		result = commonApplication.isVerifyCodeValid(telephone,"2001",verifyCode);
		if(result.containsKey("flag") && result.get("flag").equals("success")){
			//修改密码
			User u = userResource.getUserByTelephone(telephone);
			if(null == u){
				result.put("flag","fail");
				result.put("message","用户不存在");
				return  result;
			}
			int flag = userResource.modifyUserPassword(u.getId(),encoder.encode(password));
			if(flag == 1){
				result.put("flag","success");
				result.put("message","修改成功");
				return  result;
			}
		}
		result.put("flag","fail");
		result.put("message","修改失败");
		return  result;
	}


	@Override
	public int updateMobile(String id,  String newMobile) {
		return userResource.updateUserMobile(id,newMobile);
	}

	//

	public  Map<String, Object> checkVerifyCode(String newMobile ,String verifyCode){
		Map<String,Object> map = new HashMap<>();
		map = commonApplication.isVerifyCodeValid(newMobile, SendMessageConstants.NEWPHONEVERIFY,verifyCode);
		return map ;
	}

	@Override
	public List<User> checkHashAdmin(List<String> hashs) {
		return userResource.checkHashAdmin(hashs);
	}
}
