package com.ido85.party.aaaa.mgmt.internet.resource;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.internet.domain.ApplicationInternetUserRel;


public interface ApplicationUserRelResource extends JpaRepository<ApplicationInternetUserRel, Long>{

	@Query("select r from ApplicationInternetUserRel r where r.userId = :userId")
	List<ApplicationInternetUserRel> getAllRelByUserId(@Param("userId")Long userId);

}
