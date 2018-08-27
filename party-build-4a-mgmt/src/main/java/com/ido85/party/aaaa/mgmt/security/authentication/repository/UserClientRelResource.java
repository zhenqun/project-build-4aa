package com.ido85.party.aaaa.mgmt.security.authentication.repository;

import com.ido85.party.aaaa.mgmt.business.domain.ClientUserRel;
import com.ido85.party.aaaa.mgmt.dto.userinfo.AssistClientDto;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserClientRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

//import com.ido85.party.aaaa.mgmt.security.authentication.domain.ClientUserRel;

public interface UserClientRelResource extends JpaRepository<UserClientRel, Long>{

	@Query("select c.manageId from UserClientRel c where c.userId = :id and c.clientId = :clientId")
	String getManageIdByUserIdClientId(@Param("id")String id, @Param("clientId")String clientId);
	
	@Query("select c from UserClientRel c where c.userId = :userId and c.clientId = :clientId")
	List<UserClientRel> getRelByUserIdClientId(@Param("userId")String userId, @Param("clientId")String clientId);

	@Query("select c from UserClientRel c where c.userId = :userId and c.clientId = :clientId")
	UserClientRel getRelByuIdClientId(@Param("userId")String userId, @Param("clientId")String clientId);

	@Query("select c.clientId from UserClientRel c where c.userId = :userId")
	List<String> getClientIdByUserId(@Param("userId")String id);

	@Query("select c from UserClientRel c where c.userId = :userId")
	List<UserClientRel> getClientRelByUserId(@Param("userId") String id);

	@Query("select  c from UserClientRel c where c.userId in :ids and c.clientId = :clientId")
	List<UserClientRel> getClientRelByUserIds(@Param("ids") List<String> ids,@Param("clientId")String clientId);

	@Query("select c from UserClientRel c where c.userId in :ids")
	List<ClientUserRel> getRelByUserIds(@Param("ids") List<String> ids);

	@Query("select c from UserClientRel c where c.userId = :userId")
	List<UserClientRel> getAssistAllApplication(@Param("userId") String userId);
	@Query("select c from UserClientRel c where c.manageId = :orgId")
	List<UserClientRel> getRelByManageId(@Param("orgId") String oldOrgId);

	@Query("select c from UserClientRel c where c.userId = :userId and c.clientId in :clientIds ")
	List<UserClientRel> getRelByAssistUserIdClientId(@Param("userId")String userId,@Param("clientIds")List<String> clientIds);
	@Query("select c from UserClientRel c where c.userId = :userId")
	Set<UserClientRel> getSetClientRelByUserId(@Param("userId") String id);

	@Query("select c from UserClientRel c where c.userId in :ids")
	List<UserClientRel> getAllUserInfoRelByUserIds(@Param("ids") List<String> ids);

	@Transactional
	@Modifying
	@Query("update UserClientRel  set manageName = :orgName where manageId = :orgId")
	int updateOrgNameByOrgId(@Param("orgName")String orgName ,@Param("orgId")String orgId);

}
