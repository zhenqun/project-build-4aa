package com.ido85.party.aaaa.mgmt.business.resources;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.business.domain.Category;

public interface CategoryResources extends JpaRepository<Category, Long>{

	@Query("select c from Category c where c.clientId = :clientId")
	List<Category> getCategoryByClientId(@Param("clientId")String clientId);

}
