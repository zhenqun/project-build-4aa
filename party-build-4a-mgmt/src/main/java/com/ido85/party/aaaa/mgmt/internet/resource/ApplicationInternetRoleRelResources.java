package com.ido85.party.aaaa.mgmt.internet.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.internet.domain.ApplicationInternetRoleRel;

public interface ApplicationInternetRoleRelResources extends JpaRepository<ApplicationInternetRoleRel, Long>{

	/**
	 * 根据类型和appid查询角色
	 * @param string
	 * @return
	 */
	@Query("select a from ApplicationInternetRoleRel a where a.type = :type and a.applicationId = :applicationId")
	ApplicationInternetRoleRel getRoleByAppId(@Param("type")String type,@Param("applicationId")Long applicationId);
}
