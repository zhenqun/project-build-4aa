package com.ido85.party.aaaa.mgmt.business.resources;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.business.domain.OuNodeRel;

public interface OuNodeRelResources extends JpaRepository<OuNodeRel, Long>{

	@Query("select r from OuNodeRel r where r.nodeOrgId = :manageId")
	OuNodeRel getRelBy(@Param("manageId")String manageId);

}
