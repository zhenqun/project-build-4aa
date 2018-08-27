package com.ido85.party.aaaa.mgmt.security.authentication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.PasswordInfo;

public interface PasswordInfoResources extends JpaRepository<PasswordInfo, Long>{

	@Query("select p from PasswordInfo p where p.userId = :userId order by date desc")
	List<PasswordInfo> getLastInfoByUserId(@Param("userId")String id);

}
