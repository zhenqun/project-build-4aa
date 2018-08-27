package com.ido85.party.aaaa.mgmt.security.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserVpnRel;

import java.util.List;

public interface UserVpnRelResources extends JpaRepository<UserVpnRel, Long>{

	@Query("select o.ouName from UserVpnRel o where o.userId = :id")
	String getOuNameByUserId(@Param("id")String id);

	@Query("select l from UserVpnRel l where l.userId = :id")
	UserVpnRel getRelByUserId(@Param("id")String id);

	@Query("select l from UserVpnRel l where l.userId in :ids")
	List<UserVpnRel> getRelByUserIds(@Param("ids") List<String> only1List);
}
