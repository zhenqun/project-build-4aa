package com.ido85.party.aaa.authentication.reposities;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaa.authentication.entity.Role;

public interface RoleResource extends JpaRepository<Role, Long>{

	@Query("select r.name from Role r where r.clientId = :clientId")
	List<String> getRoleById(@Param("clientId")String clientId);

}
