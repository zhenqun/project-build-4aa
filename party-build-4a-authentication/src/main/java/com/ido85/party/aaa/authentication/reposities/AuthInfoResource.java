package com.ido85.party.aaa.authentication.reposities;

import com.ido85.party.aaa.authentication.application.Authentication;
import com.ido85.party.aaa.authentication.entity.AuthenticationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthInfoResource extends JpaRepository<AuthenticationInfo, Long>{

	@Query("select a from AuthenticationInfo a "
			+ "where a.clientId=:clientId and a.relName=:relName and a.roleId = :roleId and a.uniqueKey=:uniqueKey  and a.validFlag ='0'")
	AuthenticationInfo getInfoByAuthenti(@Param("clientId")String clientId, @Param("relName")String relName, 
			@Param("roleId")Long roleId, @Param("uniqueKey")String uniqueKey);

	@Query("select a from AuthenticationInfo a "
			+ "where a.clientId=:clientId and a.relName=:relName and a.roleId = :roleId and a.uniqueKey=:uniqueKey")
	AuthenticationInfo getInfoByCRRN(@Param("clientId")String clientId,
									 @Param("relName")String relName, @Param("roleId")Long roleId, @Param("uniqueKey")String uniqueKey);

	@Query("select a from AuthenticationInfo a where a.clientId = :clientId and " +
			"a.roleId = :roleId and a.userId =:userId")
	List<AuthenticationInfo> getInfoByCRU(@Param("clientId") String clientId, @Param("roleId") Long roleId, @Param("userId") String userId);

	@Query("select a from AuthenticationInfo  a where a.userId in :userIds")
    List<AuthenticationInfo> getInfoByUserIds(@Param("userIds") List<String> userIds);
}
