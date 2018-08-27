package com.ido85.party.aaaa.mgmt.security.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.SmTemplate;


public interface SmTemplateResouces extends JpaRepository<SmTemplate, Long>{

	@Query("select t from SmTemplate t where t.smTemplateType = :type")
	SmTemplate getTemplateByType(@Param("type")String type);

}
