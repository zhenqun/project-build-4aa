package com.ido85.party.aaaa.mgmt.business.application.impl;

import com.ido85.party.aaaa.mgmt.business.application.RoleApplication;
import com.ido85.party.aaaa.mgmt.business.domain.*;
import com.ido85.party.aaaa.mgmt.business.resources.*;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.DataHandleDto;
import com.ido85.party.aaaa.mgmt.dto.assist.AssistRoleAddedQueryDto;
import com.ido85.party.aaaa.mgmt.dto.assist.AssistRoleModParam;
import com.ido85.party.aaaa.mgmt.dto.assist.AssistRoleQueryDto;
import com.ido85.party.aaaa.mgmt.dto.role.*;
import com.ido85.party.aaaa.mgmt.security.authentication.application.UserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserClientRel;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserClientRelResource;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
@Named
public class RoleApplicationImpl implements RoleApplication {

	@Inject
	private IdGenerator idGenerator;
	
	@Inject
	private BusinessRoleResources businessRoleResources;
	
	@Inject
	private UserClientRelResource userClientRelResource;
	
	@Inject
	private PermissionResource permissionResource;
	
	@PersistenceContext(unitName = "business")
	private EntityManager businessEntity;

	@Inject
	private RestTemplate restTemplate;
	
	@Inject
	private CLientExpandResources cLientExpandResources;
	
	@Inject
	private UserApplication userApp;
	
	@Inject
	private CategoryResources categoryResources;

	@Inject
	private UserRoleResource userRoleResource;

	@Inject
	private RolePackageResources rolePackageResources;

	/**
	 * 创建  修改角色
	 */
	public Map<String, Object> roleManage(RoleManageParam param) {
		Map<String,Object> result = new HashMap<>();
		List<Permission> perList = null;
		BusinessRole role = null;
		String manageId = null;
		String manageName = null;		
		String roleId = param.getRoleId();
		String roleName = param.getRoleName();
		String roleDescription = param.getRoleDescription();
		List<Long> permissionIds = param.getPermissionIds();
		String detail = param.getDetail();
		String userId = null;
		String clientId = param.getClientId();
		if ("party-build-dtdj".equals(clientId)) {
			result.put("flag", "fail");
			result.put("message","内容发布系统 不能新增或修改角色");
			return result;
		}
		//查询功能权限
		if(ListUntils.isNotNull(permissionIds)){
			perList = permissionResource.getPermissionsBuIds(permissionIds);
		}
		//查询登录人的管理范围
		User user = null;
		user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(null != user){
			userId = user.getId();
		}
		//如果roleid存在则为更改，如果不存在则为新增
		if(StringUtils.isNull(roleId)){
			//查询是否重复 TODO
			role = businessRoleResources.getRoleByNameAndClientId(roleName,clientId);
			if(null != role){
				result.put("flag", "fail");
				result.put("message","角色名称重复！");
				return result;
			}
			if(null != user){
				UserClientRel rel = userClientRelResource.getRelByuIdClientId(user.getId(), clientId);
				if(null != rel){
					manageId = rel.getManageId();
					manageName = rel.getManageName();
				}
			}
			role = new BusinessRole();
			role.setDescription(roleDescription);
			role.setId(idGenerator.next());
			role.setName("ROLE_"+idGenerator.next());
			role.setManageId(manageId);
			role.setManageName(manageName);
			role.setPermissions(perList);
			role.setClientId(clientId);
			role.setCreateBy(userId);
			role.setCreateDate(new Date());
			role.setDetail(detail);
			businessRoleResources.save(role);
		}else{
			role = businessRoleResources.getRoleById(StringUtils.toLong(roleId));
			role.setDescription(roleDescription);
			role.setName(roleName);
			role.setPermissions(perList);
			role.setUpdateBy(userId);
			role.setUpdateDate(new Date());
			role.setDetail(detail);
			businessRoleResources.save(role);
		}
		result.put("flag", "success");
		result.put("message","创建成功");
		return result;
	}

	@Override
	public List<RoleQueryDto> roleQuery(RoleQueryParam param) {
		String clientId = param.getClientId();
		Integer pageNo = param.getPageNo();
		Integer pageSize = param.getPageSize();
		String des = param.getRoleDescription();
		String roleName = param.getRoleName();
		String manageId = null;
		Set<String> userIds = new HashSet<>();
		//查询登录人的管理范围
		User user = null;
		user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(null != user){
			UserClientRel rel = userClientRelResource.getRelByuIdClientId(user.getId(), clientId);
			if(null != rel){
				manageId = rel.getManageId();
			}else{
				return null;
			}
		}
		//查询
		StringBuffer sb = new StringBuffer("select new com.ido85.party.aaaa.mgmt.dto.role.RoleQueryDto"
				+ "(r.id, r.name, r.description, r.createDate, r.createBy,r.updateBy, r.updateDate,r.isCommon,r.detail) "
				+ "from BusinessRole r ");
		sb.append("where r.clientId=:clientId");
		if(!StringUtils.isNull(roleName)){
			sb.append(" and r.name like :roleName");
		}
		if(!StringUtils.isNull(des)){
			sb.append(" and r.description like :des");
		}
		if(!StringUtils.isNull(manageId)){
			sb.append(" and ((r.manageId = :manageId and r.isCommon='f') or (r.isCommon ='t')) ");
		}
		Query q = businessEntity.createQuery(sb.toString(), RoleQueryDto.class);
		System.out.println(sb.toString());
		if(!StringUtils.isNull(roleName)){
			q.setParameter("roleName", "%"+roleName+"%");
		}
		if(!StringUtils.isNull(des)){
			q.setParameter("des", "%"+des+"%");
		}
		if(!StringUtils.isNull(manageId)){
			q.setParameter("manageId", manageId);
		}
		q.setParameter("clientId", clientId);
		q.setParameter("manageId", manageId);
		q.setFirstResult((pageNo - 1) * pageSize);
		q.setMaxResults(pageSize);
		List<RoleQueryDto> dtoList = q.getResultList();
		if(ListUntils.isNotNull(dtoList)){
			for(RoleQueryDto dto:dtoList){
				String createBy = dto.getCreateBy();
				String updateBy = dto.getUpdateBy();
				if(!StringUtils.isNull(createBy)){
					userIds.add(createBy);
				}
				if(!StringUtils.isNull(updateBy)){
					userIds.add(updateBy);
				}
			}
		}
		//获取用户姓名
		if(null != userIds && userIds.size() > 0){
			Map<String, String> map = userApp.getUserByIds(userIds);
			if(null != map && map.size() > 0){
				for(RoleQueryDto dto:dtoList){
					dto.setCreateBy(map.get(dto.getCreateBy()));
					dto.setUpdateBy(map.get(dto.getUpdateBy()));
				}
			}
		}
		return dtoList;
	}

	@Override
	public Long roleQueryCnt(RoleQueryParam param) {
		String clientId = param.getClientId();
		String des = param.getRoleDescription();
		String roleName = param.getRoleName();
		String manageId = null;
		//查询登录人的管理范围
		User user = null;
		user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(null != user){
			UserClientRel rel = userClientRelResource.getRelByuIdClientId(user.getId(), clientId);
			if(null != rel){
				manageId = rel.getManageId();
			}else{
				return 0L;
			}
		}
		//查询
		StringBuffer sb = new StringBuffer("select count(r.id) "
				+ "from BusinessRole r where r.clientId=:clientId");
		if(!StringUtils.isNull(roleName)){
			sb.append(" and r.name like :roleName");
		}
		if(!StringUtils.isNull(des)){
			sb.append(" and r.description like :des");
		}
		if(!StringUtils.isNull(manageId)){
			sb.append(" and r.manageId = :manageId");
		}
		Query q = businessEntity.createQuery(sb.toString());
		if(!StringUtils.isNull(roleName)){
			q.setParameter("roleName", "%"+roleName+"%");
		}
		if(!StringUtils.isNull(des)){
			q.setParameter("des", "%"+des+"%");
		}
		if(!StringUtils.isNull(manageId)){
			q.setParameter("manageId", manageId);
		}
		q.setParameter("clientId", clientId);
		q.setParameter("manageId", manageId);
		return StringUtils.toLong(q.getSingleResult());
	}

	/**
	 * 角色删除
	 */
	public boolean roleDel(String roleId) {
		Long id = StringUtils.toLong(roleId);
		BusinessRole role = businessRoleResources.getRoleById(id);
		if(null != role){
			businessRoleResources.delete(role);
			//删除角色关联的用户与角色关系
			List<UserRole> list = userRoleResource.getRelByRoleId(id);
			userRoleResource.delete(list);
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 查询该登录用户在该应用下的所有权限列表
	 * @return
	 */
	public List<CategoryPermissionDto> permissionQuery(String clientId) {
		//查询登录人的管理范围
		String manageId = null;
		List<CategoryPermissionDto> dtoList = null;
		List<Permission> permissions = null;
		List<PermissionQueryDto> permissionDtos = null;
		CategoryPermissionDto cateDto = null;
		PermissionQueryDto dto = null;
		boolean isLimit = false;
		String permissionManageId = null;
		User user = null;
		user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(null != user){
			UserClientRel rel = userClientRelResource.getRelByuIdClientId(user.getId(), clientId);
			if(null != rel){
				manageId = rel.getManageId();
			}else{
				return null;
			}
		}
		List<Category> categories = categoryResources.getCategoryByClientId(clientId);
//		StringBuffer sb = new StringBuffer("select t.permissionId,t.permissionName,t.permissionDescription,t.manageId,t.isLimit "
//				+ "from Permission t where t.clientId=:clientId");
//		Query q = businessEntity.createQuery(sb.toString());
//		q.setParameter("clientId", clientId);
//		List<Object[]> oList = q.getResultList();
		if(ListUntils.isNotNull(categories)){
			dtoList = new ArrayList<>();
			for(Category category:categories){
				cateDto = new CategoryPermissionDto();
				cateDto.setCategoryId(StringUtils.toString(category.getCategoryId()));
				cateDto.setCategoryName(category.getCategoryName());
				permissions = category.getPermissions();
				if(ListUntils.isNotNull(permissions)){
					permissionDtos = new ArrayList<>();
					for(Permission p:permissions){
						dto = new PermissionQueryDto();
						dto.setPermissionDescription(p.getPermissionDescription());
						dto.setPermissionId(StringUtils.toString(p.getPermissionId()));
						dto.setPermissionName(p.getPermissionName());
						isLimit = p.isLimit();
						if(isLimit){
							if(dto.getPermissionName().equals("PERMISSION_PARTY_EDU_RESOURCE")){
								if(manageId.equals("030b9e46-b8ea-47ec-9feb-fb8c3eead801")){
									dto.setIsLimit("0");
								}else{
									dto.setIsLimit("1");
								}
							}
//							permissionManageId = StringUtils.toString(p.getManageId());
//							boolean flag = persimissionCheck(clientId,permissionManageId,manageId);
//							if(flag==false){
//								dto.setIsLimit("1");
//							}
						}else{
							dto.setIsLimit("0");
						}
						permissionDtos.add(dto);
					}
					cateDto.setPermissions(permissionDtos);
				}
				dtoList.add(cateDto);
			}
		}
		return dtoList;
	}

	@Override
	public boolean persimissionCheck(String clientId, String permissionManageId,String userManageId) {
		String url = null;
		//查询该应用的url
		ClientExpand ce = cLientExpandResources.getClientById(clientId);
		if(null != ce){
			url = ce.getCheckUrl()+"?firstId="+permissionManageId+"&secondId="+userManageId;
		}
		String flag = restTemplate.getForObject(url, String.class);
		if(flag.equals("0")){
			return true;
		}
		return false;
	}

	/**
	 * 查询角色授权详情
	 * @return
	 */
	public List<RolePermissionQueryDto> rolePermissionQuery(String roleId) {
		List<RolePermissionQueryDto> dtoList = null;
		RolePermissionQueryDto dto = null;
		BusinessRole role = businessRoleResources.getRoleById(StringUtils.toLong(roleId));
		List<Permission> pers = role.getPermissions();
		if(ListUntils.isNotNull(pers)){
			dtoList = new ArrayList<>();
			for(Permission p:pers){
				dto = new RolePermissionQueryDto();
				dto.setPermissionDescription(p.getPermissionDescription());
				dto.setPermissionId(StringUtils.toString(p.getPermissionId()));
				dto.setPermissionName(p.getPermissionName());
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	@Override
	public List<DataHandleDto> getRoleUserById(String userId) {
		List<DataHandleDto> dtoList = new ArrayList<>();
		StringBuffer str = new StringBuffer("select r.client_id,t.manage_id from r_4a_user_role t,t_4a_role r where t.role_id=r.id and t.user_id = :userId");
		Query query = businessEntity.createNativeQuery(str.toString());
		query.setParameter("userId",userId);
		List<Object[]> objectList = query.getResultList();
		DataHandleDto dto=null;
		if(null != objectList && objectList.size()>0){
			for(Object[] object:objectList){
				dto = new DataHandleDto();
				dto.setClientId(StringUtils.toString(object[0]));
				dto.setManageId(StringUtils.toString(object[1]));
				dtoList.add(dto);
			}
		return dtoList;
		}
		return null;
	}

	/**
	 * 获取安全员能看到的所有角色(辅助安全员模块使用)
	 * @return
	 */
	public List<AssistRoleQueryDto> assistRoleQuery() {
		List<AssistRoleQueryDto> dtoList = null;
		AssistRoleQueryDto assistRoleQueryDto = null;
		//查询
		StringBuffer sb = new StringBuffer("select "
				+ "r.clientId, c.clientName, r.description, r.detail, r.isCommon, r.id "
				+ "from BusinessRole r,ClientExpand c where r.clientId = c.clientId and r.isCommon ='t'");
		Query q = businessEntity.createQuery(sb.toString());
		List<Object[]> oList = q.getResultList();
		if(ListUntils.isNotNull(oList)){
			dtoList = new ArrayList<>();
			for(Object[] o:oList){
				assistRoleQueryDto = new AssistRoleQueryDto();
				assistRoleQueryDto.setClientId(StringUtils.toString(o[0]));
				assistRoleQueryDto.setClientName(StringUtils.toString(o[1]));
				assistRoleQueryDto.setDescription(StringUtils.toString(o[2]));
				assistRoleQueryDto.setDetail(StringUtils.toString(o[3]));
				assistRoleQueryDto.setIsCommon(StringUtils.toString(o[4]).equals("true")?"1":"0");
				assistRoleQueryDto.setRoleId(StringUtils.toString(o[5]));
				dtoList.add(assistRoleQueryDto);
			}
		}
		return dtoList;
	}

	/**
	 * 角色包保存更新(辅助安全员模块使用)
	 * @param param
	 * @return
	 */
	public Map<String, String> assistRoleMod(List<AssistRoleModParam> param) {
		//查询出所有角色包角色，改为已经删除
		//重新添加新的
		Map<String,String> map = new HashMap<>();
		User user = null;
		RolePackage rolePackage = null;
		List<RolePackage> packages = null;
		List<String> manageIds = null;
		user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		user = userApp.getUserByHash("xg818pkdZ3KzIJSt8CT5Nq/wb04=");
		if(null != user){
			List<UserClientRel> rels = userClientRelResource.getClientRelByUserId(user.getId());
			if(ListUntils.isNotNull(rels)){
				manageIds = new ArrayList<>();
				for(UserClientRel rel:rels){
					if(!StringUtils.isNull(rel.getManageId())){
						map.put(rel.getClientId(),rel.getManageId());
						manageIds.add(rel.getManageId());
					}
				}
			}
			if(ListUntils.isNotNull(param)){
				packages = new ArrayList<>();
				for(AssistRoleModParam p:param){
					if(!StringUtils.isNull(map.get(p.getClientId()))){
						rolePackage = new RolePackage();
						rolePackage.setClientId(p.getClientId());
						rolePackage.setCreateBy(user.getId());
						rolePackage.setCreateDate(new Date());
						rolePackage.setDelBy(null);
						rolePackage.setDelDate(null);
						rolePackage.setDelFlag("0");
						rolePackage.setId(idGenerator.next());
						rolePackage.setManageId(map.get(p.getClientId()));
						rolePackage.setRoleId(Long.parseLong(p.getRoleId()));
						rolePackage.setUpdateBy(null);
						rolePackage.setUpdateDate(null);
						packages.add(rolePackage);
					}
				}
				//查找该安全员所有应用下的角色包设置，置为delflag为1，然后重新保存新的关联
				List<RolePackage> packageList = rolePackageResources.getRelByManageId(manageIds);
				for(RolePackage t:packageList){
					t.setDelFlag("1");
					t.setDelBy(user.getId());
					t.setDelDate(new Date());
				}
				rolePackageResources.save(packageList);
				rolePackageResources.save(packages);
			}else{
				List<RolePackage> packageList = rolePackageResources.getRelByManageId(manageIds);
				for(RolePackage t:packageList){
					t.setDelFlag("1");
					t.setDelBy(user.getId());
					t.setDelDate(new Date());
				}
				rolePackageResources.save(packageList);
			}
		}
		map.put("flag","success");
		map.put("message","添加成功!");
		return map;
	}

	/**
	 * 获取保存过的角色包配置
	 * @return
	 */
	public List<AssistRoleAddedQueryDto> assistRoleAddedQuery() {
		AssistRoleAddedQueryDto assistRoleAddedQueryDto = null;
		List<AssistRoleAddedQueryDto> dtoList = new ArrayList<>();
		List<String> manageIds = null;
		List<RolePackage> list = null;
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		User user = userApp.getUserByHash("xg818pkdZ3KzIJSt8CT5Nq/wb04=");
		if(null != user) {
			List<UserClientRel> rels = userClientRelResource.getClientRelByUserId(user.getId());
			if (ListUntils.isNotNull(rels)) {
				manageIds = new ArrayList<>();
				for (UserClientRel rel : rels) {
					if (!StringUtils.isNull(rel.getManageId())) {
						manageIds.add(rel.getManageId());
					}
				}
			}
			list = rolePackageResources.assistRoleAddedQuery(manageIds);
			if(ListUntils.isNotNull(list)){
				for(RolePackage rolePackage:list){
					assistRoleAddedQueryDto = new AssistRoleAddedQueryDto();
					assistRoleAddedQueryDto.setClientId(rolePackage.getClientId());
					assistRoleAddedQueryDto.setRoleId(StringUtils.toString(rolePackage.getRoleId()));
					dtoList.add(assistRoleAddedQueryDto);
				}
			}
		}
		return dtoList;
	}

}
