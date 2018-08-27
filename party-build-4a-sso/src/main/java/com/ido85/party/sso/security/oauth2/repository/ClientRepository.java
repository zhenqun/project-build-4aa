/**
 * 
 */
package com.ido85.party.sso.security.oauth2.repository;

import com.ido85.party.sso.platform.data.datasource.TargetDataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sso.security.oauth2.domain.PlatformClientDetails;

/**
 * oauth客户端操作资源库
 * @author rongxj
 *
 */
public interface ClientRepository extends JpaRepository<PlatformClientDetails, String>{

	@Query("select c from PlatformClientDetails c where c.clientId = :clientId")
	PlatformClientDetails getClientByClientId(@Param("clientId")String clientId);
	
}
