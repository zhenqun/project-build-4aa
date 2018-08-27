package com.ido85.party.sso.security.authentication.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sso.security.authentication.domain.AuthenticationInfo;

public interface AuthenticationInfoResources extends JpaRepository<AuthenticationInfo, Long>{

	@Modifying
	@Transactional
	@Query("update AuthenticationInfo a set a.delFlag = '1' where a.authenticationInfoId in :ids")
	void updateAuthencationState(@Param("ids")List<Long> authencationInfoIds);

	@Query("select i from AuthenticationInfo i where i.idCard = :idCard and i.uniqueKey =:key" +
			" and i.relName = :relName and i.delFlag = '0' and i.validFlag = '0'")
	List<AuthenticationInfo> getAuthByIdcardkeyrelname(@Param("idCard") String idCard,
													   @Param("key") String uniquekey, @Param("relName") String relName);
}
