package com.ido85.party.sso.security.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sso.security.authentication.domain.UserVpnRel;

public interface UserVpnResources extends JpaRepository<UserVpnRel, Long>{

	@Query("select l from UserVpnRel l where l.userId = :id and l.vpn = :vpn")
	UserVpnRel getRelByUserId(@Param("id")String id, @Param("vpn")String vpn);

	@Query("select l from UserVpnRel l where l.userId = :id")
	UserVpnRel getRelById(@Param("id")String id);

	@Query("select l.ou from UserVpnRel l where l.userId = :id")
	String getOuByByUserId(@Param("id")String userId);

}
