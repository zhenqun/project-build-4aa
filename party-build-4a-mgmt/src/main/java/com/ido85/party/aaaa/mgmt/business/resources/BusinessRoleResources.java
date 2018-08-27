package com.ido85.party.aaaa.mgmt.business.resources;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.business.domain.BusinessRole;

public interface BusinessRoleResources extends JpaRepository<BusinessRole, Long>{

	@Query("select r from BusinessRole r where r.name = :name")
	BusinessRole getRoleByName(@Param("name")String name);
	
	@Query("select r from BusinessRole r where r.id = :id")
	BusinessRole getRoleById(@Param("id")Long id);

	@Query("select r from BusinessRole r where r.name = :name and r.clientId=:clientId")
	BusinessRole getRoleByNameAndClientId(@Param("name")String roleName, @Param("clientId")String clientId);

}
