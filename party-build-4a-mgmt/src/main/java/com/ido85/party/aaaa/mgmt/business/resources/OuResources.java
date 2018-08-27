package com.ido85.party.aaaa.mgmt.business.resources;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ido85.party.aaaa.mgmt.business.domain.Ou;

public interface OuResources extends JpaRepository<Ou, Long>{

	@Query("select o from Ou o")
	List<Ou> getAllOu();

}
