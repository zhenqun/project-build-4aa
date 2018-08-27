package com.ido85.party.aaaa.mgmt.business.resources;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ido85.party.aaaa.mgmt.business.domain.PlatformClientDetails;

public interface CLientResources extends JpaRepository<PlatformClientDetails, String>{

	@Query("select c from PlatformClientDetails c where c.isDisplay = 't' ")
	List<PlatformClientDetails> getAllClient();

}
