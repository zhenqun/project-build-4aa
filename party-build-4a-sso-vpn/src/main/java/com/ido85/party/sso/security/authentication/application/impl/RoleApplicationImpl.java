package com.ido85.party.sso.security.authentication.application.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.collection.internal.PersistentSet;

import com.ido85.party.sso.dto.ClientRolesDto;
import com.ido85.party.sso.dto.RoleDto;
import com.ido85.party.sso.security.authentication.application.RoleApplication;
import com.ido85.party.sso.security.authentication.domain.Resource;
import com.ido85.party.sso.security.authentication.domain.Role;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.repository.ClientRoleRelResources;
import com.ido85.party.sso.security.utils.ListUntils;
import com.ido85.party.sso.security.utils.StringUtils;
@Named
public class RoleApplicationImpl implements RoleApplication {

	@Inject
	private EntityManager em;
	
	@Inject
	private ClientRoleRelResources clientRoleRelResources;
	
	/**
	 * 获取用户角色信息
	 */
//	public Set<Role> getUserRoleClients(User user,String clientId) {
//		List<ClientRolesDto> clients = new ArrayList<ClientRolesDto>();
//		StringBuffer sql = new StringBuffer("SELECT DISTINCT\n" +
//				"	d.client_id AS clientId,\n" +
//				"	d.client_name AS clientName,\n" +
//				"	array_to_string(array_agg(r.\"name\"), ',') as roleNames,\n" +
//				"   array_to_string(ARRAY_AGG(r.\"id\"), ',') AS roleIds\n" +
//				"FROM\n" +
//				"	r_4a_client_user_rel cur,\n" +
//				"	r_4a_user_role ur,\n" +
//				"	t_4a_role r,\n" +
//				"	r_4a_client_role_rel crr,\n" +
//				"	oauth_client_details d\n" +
//				"WHERE\n" +
//				"	ur.user_id = :userId\n" +
//				"AND ur.role_id = r.\"id\"\n" +
//				"AND crr.client_id = cur.client_id\n" +
//				"AND crr.role_id = ur.role_id\n" +
//				"AND cur.client_id = d.client_id\n" +
//				"GROUP BY\n" +
//				"	d.client_id");
//		Query query = em.createNativeQuery(sql.toString());
//		query.setParameter("userId", userId);
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
//				clientRolesDto.setClientName(StringUtils.toString(object[1]));
//				roleNames = StringUtils.toString(object[2]);
//				roleIds = StringUtils.toString(object[3]);
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
//				}
//				clientRolesDto.setRoles(roles);
//				clients.add(clientRolesDto);
//			}
//		}
//		Set<Role> clientRoles = getRolesByClientId(clientId);
//		Set<Role> roles = user.getRoles();
//		Set<Role> result = null;
//		if(null != clientRoles && clientRoles.size() > 0 
//				&& null != roles && roles.size() > 0){
//			result = sameSet(roles,clientRoles);
//		}
//		return result;
//	}

	/**
	 * 根据clientID获取角色
	 * @param clientId
	 * @return
	 */
	private Set<Role> getRolesByClientId(String clientId) {
		StringBuffer sb = new StringBuffer("SELECT\n" +
				"	r.*\n" +
				"FROM\n" +
				"	r_4a_client_role_rel crr,\n" +
				"	t_4a_role r\n" +
				"WHERE\n" +
				"	crr.role_id = r.\"id\"");
		if(!StringUtils.isNull(clientId)){
			sb.append(" and crr.client_id = :clientId");
		}
		Query q = em.createNativeQuery(sb.toString(), Role.class);
		if(!StringUtils.isNull(clientId)){
			q.setParameter("clientId", clientId);
		}
		Set<Role> set = new HashSet<Role>();  
		set.addAll(q.getResultList());
		return set;
	}

	/*
     * sameSet方法返回并集
     */
    public Set<Role> sameSet(Set<Role> roles , Set<Role> clientRoles){
    	Set<Role> sameSet = new HashSet<Role>();
    	HashSet<Role> hashs = (HashSet<Role>)roles;
        /*
         * 利用ForEach循环和HashSet中的contains方法判断两个Set中元素是否相交
         * 相交则存入SameSet中
         */
        for(Role s : hashs){
            if(clientRoles.contains(s))
                sameSet.add(s);
        }
        return sameSet;
    }
}
