package com.ido85.party.aaaa.mgmt.internet.resource;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ido85.party.aaaa.mgmt.internet.domain.InternetApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.Application;

public interface InternetApplicationResources extends JpaRepository<InternetApplication, Long>{

	@Query("select a from InternetApplication a")
	List<InternetApplication> getAllApplication();

}
