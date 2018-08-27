package com.ido85.party.sso.security.authentication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sso.security.authentication.domain.Org;

public interface OrgResources extends JpaRepository<Org, String> {
	@Query("SELECT u from Org u where u.parentId = :id order by u.orderId")
	List<Org> getOrg(@Param("id") String id);
	
	@Query("SELECT u from Org u where u.orgName = :name")
	List<Org> getOrgByName(@Param("name") String name);

	@Query("select u from Org u where u.parentId is null")
	List<Org> getFirstOrg();

	@Query("select u from Org u where u.id = :id")
	Org getOrgById(@Param("id")String orgId);
}
