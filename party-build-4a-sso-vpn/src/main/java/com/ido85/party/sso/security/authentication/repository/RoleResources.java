/**
 * 
 */
package com.ido85.party.sso.security.authentication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sso.security.authentication.domain.Role;

/**
 * role 资源类
 * @author fire
 *
 */
public interface RoleResources extends JpaRepository<Role, Long> {

	@Query(" SELECT u from Role u ")
	List<Role> queryRole();
	
	@Query(" SELECT u from Role u where u.name = :name ")
	List<Role> queryRoleByName(@Param("name")String name);
	
}
