package com.ido85.party.sso.security.authentication.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sso.security.authentication.domain.ApplicationUserRel;

public interface ApplicationUserRelResources extends JpaRepository<ApplicationUserRel, Long> {

	@Query("select u.applicationId from ApplicationUserRel u where u.userId = :userId")
	List<Long> getApplicationIdByUserId(@Param("userId")String id);
	
}
