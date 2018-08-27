package com.ido85.party.aaaa.mgmt.internet.application.impl;

import java.util.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.AllotApplicationDto;
import com.ido85.party.aaaa.mgmt.dto.ApplicationAllotParam;
import com.ido85.party.aaaa.mgmt.internet.application.InternetUserApplication;
import com.ido85.party.aaaa.mgmt.internet.domain.*;
import com.ido85.party.aaaa.mgmt.internet.dto.ApplicationDto;
import com.ido85.party.aaaa.mgmt.internet.dto.IntegerUserDto;
import com.ido85.party.aaaa.mgmt.internet.dto.IntegerUserQueryParam;
import com.ido85.party.aaaa.mgmt.internet.resource.*;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.Application;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.ApplicationUserRel;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.Role;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserResources;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Named
@Slf4j
public class InternetUserApplicationImpl implements InternetUserApplication {
	
//	@PersistenceContext(unitName = "internet")
	private EntityManager internetEntity;
	
	@PersistenceContext(unitName = "business")
	private EntityManager businessEntity;
	
	@Inject
	private InternetUserResources userResource;
	
	@Inject
	private ApplicationUserRelResource appUserRelResource;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Inject
	private InternetRoleResources internetRoleResources;
	
	@Inject
	private ApplicationInternetRoleRelResources appRoleRelResources;
	
	@Inject
	private InternetApplicationResources internetApplicationResources;


	@Inject
	private UpdateUserTeleLogResources updateUserTeleLogResources;
	
	@Override
	public List<IntegerUserDto> getInternetUserByCondition(IntegerUserQueryParam param) {
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
		Query query = internetEntity.createNativeQuery(sql.toString()); 
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
		List<IntegerUserDto> dtoList = new ArrayList<IntegerUserDto>();
		List<Object[]> objectList = query.getResultList();
		IntegerUserDto dto = null;
		if(null != objectList && objectList.size()>0){
			for(Object[] object:objectList){
				dto = new IntegerUserDto();
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

	/**
	 * 网站会员状态修改
	 */
	public boolean modifyInternetUserStatus(Long userId, Integer status) {
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
	 * 网站会员应用分配
	 */
	@Transactional
	public boolean allotInternetUserApp(ApplicationAllotParam param) {
		List<AllotApplicationDto> appList = param.getApplications();
		Long userId = param.getUserId();
		List<ApplicationInternetUserRel> entities = new ArrayList<ApplicationInternetUserRel>();
		InternetUser user = userResource.findOne(userId);
		//删除分配关系
		List<ApplicationInternetUserRel> rels = appUserRelResource.getAllRelByUserId(userId);
		appUserRelResource.delete(rels);
		//增加分配关系
		Set<InternetRole> roles = new HashSet<InternetRole>();
 		if(ListUntils.isNotNull(appList)){
			ApplicationInternetUserRel rel = null;
			for(AllotApplicationDto dto:appList){
				rel = new ApplicationInternetUserRel();
				rel.setApplicationId(dto.getApplicationId());
				rel.setApplicationUserId(idGenerator.next());
				rel.setUserId(userId);
				entities.add(rel);
				
				//增加普通会员角色
				ApplicationInternetRoleRel appRoleRel = appRoleRelResources.getRoleByAppId("1",dto.getApplicationId());
				if(null != appRoleRel){
					InternetRole role = internetRoleResources.getOne(appRoleRel.getRoleId());
					roles.add(role);
				}
			}
			appUserRelResource.save(entities);
			user.setRoles(roles);
		}
 		userResource.save(user);
		return true;
	}

	/**
	 * 网站会员查询总数
	 */
	public Long getInternetUserCntByCondition(IntegerUserQueryParam param) {
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
		Query query = internetEntity.createNativeQuery(sql.toString()); 
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
	public InternetUser getInternetUserById(Long userId) {
		return userResource.getOne(userId);
	}

	@Override
	public void saveInternetUser(InternetUser user) {
		userResource.save(user);
	}

	/**
	 * 查询所有应用
	 * @return
	 */
	public List<ApplicationDto> applicationQuery() {
	    List<InternetApplication> applications	= internetApplicationResources.getAllApplication();
	    List<ApplicationDto> dtoList = null;
	    if(ListUntils.isNotNull(applications)){
	    	dtoList = new ArrayList<ApplicationDto>();
	    	for(InternetApplication application:applications){
	    		ApplicationDto dto = new ApplicationDto();
	    		dto.setApplicationId(application.getApplicationId());
	    		dto.setApplicationName(application.getApplicationName());
	    		dtoList.add(dto);
	    	}
	    }
		return dtoList;
	}


	@Override
	@Transactional
	public void addUpdateUserTeleLog(String oldTele, String newTele, String name,
									 String idCard, String hash,String updateHash, String updateBy, String updateName,String verifyCode) {

		int count=0;
		count =userResource.updateUserTele(newTele,hash,new Date());
		log.info("此次更新的手机个数："+count+"更新前手机号"+oldTele+"更新后手机号"
				+newTele+"更新人"+updateBy+"==="+updateName+"更新时间"+new Date()+"手机验证码"+verifyCode);
		//新增更新记录
		UpdateUserTeleLog  teleLog = new UpdateUserTeleLog();

		teleLog.setId(StringUtils.toString(idGenerator.next()));
		teleLog.setOldTelephone(oldTele);
		teleLog.setNewTelephone(newTele);
		teleLog.setIdCard(idCard);
		teleLog.setRelName(name);
		teleLog.setCreateDate(new Date());
		teleLog.setIsSuccess("0");
		teleLog.setUpdateBy(updateBy);
		teleLog.setUpdateDate(new Date());
		teleLog.setVerifyCode(verifyCode);
		teleLog.setUpdateHash(updateHash);
		teleLog.setUpdateName(updateName);
		updateUserTeleLogResources.save(teleLog);
	}

	@Override
	public List<Object[]> updateTeleByAdminToSimple(String hash) {
		return userResource.updateTeleByAdminToSimple(hash);
	}

}
