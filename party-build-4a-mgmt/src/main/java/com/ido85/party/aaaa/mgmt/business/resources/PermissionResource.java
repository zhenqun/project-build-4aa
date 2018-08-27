package com.ido85.party.aaaa.mgmt.business.resources;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.business.domain.Permission;

public interface PermissionResource extends JpaRepository<Permission, Long>{

	@Query("select p from Permission p where p.permissionId in :permissionIds")
	List<Permission> getPermissionsBuIds(@Param("permissionIds")List<Long> permissionIds);

}
