package com.ido85.party.aaaa.mgmt.security.authentication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.Application;

public interface ApplicationResource extends JpaRepository<Application, Long>{

	@Query("select a from Application a")
	List<Application> getAllApplication();

}
