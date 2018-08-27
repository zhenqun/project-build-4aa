package com.ido85.party.aaaa.mgmt.business.application.impl;

import com.ido85.party.aaaa.mgmt.business.application.BusinessClientApplication;
import com.ido85.party.aaaa.mgmt.business.domain.ClientExpand;
import com.ido85.party.aaaa.mgmt.business.dto.ClientRolesDto;
import com.ido85.party.aaaa.mgmt.business.resources.CLientExpandResources;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserClientRelResource;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
@Named
public class BusinessClientApplicationImpl implements BusinessClientApplication {

	@PersistenceContext(unitName = "business")
	private EntityManager em;
	
	@Inject
	private UserClientRelResource userClientResource;

	@Inject
	private CLientExpandResources cLientExpandResources;

//	@Override
//	public List<ClientRolesDto> getClientsRoles() {
//		//查询登录人的管理范围
//		User user = null;
//		user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		List<String> clientIds = null;
//		if(null != user){
//			clientIds = userClientResource.getClientIdByUserId(user.getId());
//		}
//		if(ListUntils.isNull(clientIds)){
//			return null;
//		}
//		List<ClientRolesDto> clients = new ArrayList<ClientRolesDto>();
//		StringBuffer sql = new StringBuffer("SELECT DISTINCT\n" +
//				"					e.client_id AS clientId,\n" +
//				"					e.client_name AS clientName,\n" +
//				"					e.level_url as classificationUrl,\n" +
//				"				array_to_string(ARRAY_AGG(r.\"description\"), ',') AS roleNames,\n" +
//				"				array_to_string(ARRAY_AGG(r.\"id\"), ',') AS roleIds\n" +
//				"				FROM\n" +
//				"				client_expand e \n" +
////				"				LEFT JOIN r_4a_client_role_rel crr ON e.client_id = crr.client_id\n" +
//				"				LEFT JOIN t_4a_role r ON e.client_id = r.client_id\n" +
//				"				where e.is_display='t' and e.client_id in:clientIds\n" +
//				"				GROUP BY\n" +
//				"					e.client_id,e.client_name,e.level_url");
//		Query query = em.createNativeQuery(sql.toString());
//		query.setParameter("clientIds", clientIds);
//		@SuppressWarnings("unchecked")
//		List<Object[]> list = query.getResultList();
//		ClientRolesDto clientRolesDto = null;
//		List<RoleDto> roles = null;
//		String roleNames = "";
//		String roleIds = "";
//		RoleDto roleDto = null;
//		if(ListUntils.isNotNull(list)){
//			for(Object[] object:list){
//				clientRolesDto = new ClientRolesDto();
//				clientRolesDto.setClientId(StringUtils.toString(object[0]));
//				if(StringUtils.isNull(StringUtils.toString(object[1]))){
//					clientRolesDto.setClientName(StringUtils.toString(object[0]));
//				}else{
//					clientRolesDto.setClientName(StringUtils.toString(object[1]));
//				}
//				clientRolesDto.setClassificationUrl(StringUtils.toString(object[2]));
//				roleNames = StringUtils.toString(object[3]);
//				roleIds = StringUtils.toString(object[4]);
//				if(!StringUtils.isNull(roleNames) && !StringUtils.isNull(roleIds)){
//					String[] roleNameArr = roleNames.split(",");
//					String[] roleIdArr = roleIds.split(",");
//					roles = new ArrayList<RoleDto>();
//					for(int i=0;i<roleNameArr.length;i++){
//						roleDto = new RoleDto();
//						roleDto.setRoleId(StringUtils.toLong(roleIdArr[i]));
//						roleDto.setRoleName(roleNameArr[i]);
//						roles.add(roleDto);
//					}
//					clientRolesDto.setRoles(roles);
//				}
//
//				clients.add(clientRolesDto);
//			}
//		}
//		return clients;
//	}
	@Override
		public List<ClientRolesDto> getClientsRoles() {
		//查询登录人的管理范围
		User user = null;
		user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<String> clientIds = null;
		if (null != user) {
			clientIds = userClientResource.getClientIdByUserId(user.getId());
		}
		if (ListUntils.isNull(clientIds)) {
			return null;
		}
		List<ClientRolesDto> clients = new ArrayList<ClientRolesDto>();
		ClientRolesDto clientRolesDto = null;
		List<ClientExpand> ceList = cLientExpandResources.getClientByIds(clientIds);
		if(ListUntils.isNotNull(ceList)){
			for(ClientExpand ce:ceList){
				clientRolesDto = new ClientRolesDto();
				clientRolesDto.setOrder(StringUtils.toInteger(ce.getOrder()));
				clientRolesDto.setClientId(ce.getClientId());
				clientRolesDto.setClientName(ce.getClientName());
				clientRolesDto.setRoles(null);
				clientRolesDto.setIfCanCreateRole(ce.getCreateRole());
				if(StringUtils.isNull(ce.getSearchUrl())){
					clientRolesDto.setIfCanSearch("0");
				}else{
					clientRolesDto.setIfCanSearch("1");
				}
				clients.add(clientRolesDto);
			}
		}
		return clients;
	}

}
