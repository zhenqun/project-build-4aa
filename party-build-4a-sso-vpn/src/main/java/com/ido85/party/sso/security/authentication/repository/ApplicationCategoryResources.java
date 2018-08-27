package com.ido85.party.sso.security.authentication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sso.security.authentication.domain.ApplicationCategory;

public interface ApplicationCategoryResources extends JpaRepository<ApplicationCategory, Long>{

	@Query("select c from ApplicationCategory c where c.delFlag = '0'")
	List<ApplicationCategory> getApplications();

	@Query("select c from ApplicationCategory c where c.delFlag = '0' and c.type = :type")
	List<ApplicationCategory> getApplicationsByType(@Param("type")String type);

}
