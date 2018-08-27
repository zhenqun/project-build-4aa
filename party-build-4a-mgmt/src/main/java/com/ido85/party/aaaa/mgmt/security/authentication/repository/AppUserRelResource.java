package com.ido85.party.aaaa.mgmt.security.authentication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.ApplicationUserRel;

public interface AppUserRelResource extends JpaRepository<ApplicationUserRel, Long>{

	@Query("select r from ApplicationUserRel r where r.userId = :userId")
	List<ApplicationUserRel> getAllRelByUserId(@Param("userId")Long userId);

}
