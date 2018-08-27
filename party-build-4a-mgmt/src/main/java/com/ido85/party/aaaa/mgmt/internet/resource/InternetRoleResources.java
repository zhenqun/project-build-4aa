package com.ido85.party.aaaa.mgmt.internet.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.internet.domain.InternetRole;

public interface InternetRoleResources extends JpaRepository<InternetRole, Integer>{

	@Query("select r from InternetRole r where r.name = :name")
	InternetRole getRoleByName(@Param("name")String name);

	@Query("select t from InternetRole t where t.id = :id")
	InternetRole getRoleById(@Param("id") Long roleId);
}
