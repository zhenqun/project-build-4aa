package com.ido85.party.aaaa.mgmt.business.resources;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.business.domain.ClientUserRel;


public interface ClientUserRelResources extends JpaRepository<ClientUserRel, Long>{

	@Modifying
	@Transactional
	@Query("delete from ClientUserRel r where r.userId = :userId")
	void deleteRelByUserId(@Param("userId")String userId);

	@Query("select c from ClientUserRel c,com.ido85.party.aaaa.mgmt.business.domain.UserRole r,com.ido85.party.aaaa.mgmt.business.domain.BusinessRole l " +
			"where c.userId = r.userId and c.clientId=:clientId and r.roleId = l.id and l.clientId = :clientId and c.userId = :userId" )
	List<ClientUserRel> getCurByClientUser(@Param("clientId")String clientId, @Param("userId")String userId);

	@Query("select c from ClientUserRel c where c.userId = :userId and c.clientId = :clientId")
	String getManageIdByUserIdClientId(@Param("userId") String id, @Param("clientId") String clientId);

	@Query("select c from ClientUserRel c where c.userId in :ids")
	List<ClientUserRel> getRelByUserIds(@Param("ids") List<String> ids);

	@Query("select c from ClientUserRel c where c.userId in :ids and c.clientId = :clientId")
	List<ClientUserRel> getRelByUserIdsClientId(@Param("ids") List<String> ids, @Param("clientId") String clientId);
}
