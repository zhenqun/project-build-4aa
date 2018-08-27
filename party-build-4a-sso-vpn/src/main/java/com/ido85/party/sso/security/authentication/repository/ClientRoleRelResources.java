package com.ido85.party.sso.security.authentication.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sso.security.authentication.domain.ClientRoleRel;
import com.ido85.party.sso.security.authentication.domain.Role;

public interface ClientRoleRelResources extends JpaRepository<ClientRoleRel, Long>{

	/**
	 * 根据clientID获取角色
	 * @param clientId
	 * @return
	 */
	@Query("select l from ClientRoleRel l where l.clientId = :clientId")
	Set<Role> getRolesByClientId(@Param("clientId")String clientId);

}
