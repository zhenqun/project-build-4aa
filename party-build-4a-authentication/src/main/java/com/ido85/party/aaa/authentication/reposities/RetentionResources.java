package com.ido85.party.aaa.authentication.reposities;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaa.authentication.entity.Retention;

import java.util.List;


public interface RetentionResources extends JpaRepository<Retention, Long>{

	@Query("select r from Retention r where r.idCard = :idCard and r.relName = :name")
	Retention getRetentionByIdcardAndName(@Param("idCard")String idCard, @Param("name")String name);

	@Query("select r from Retention r where r.idCard = :idCard and r.relName = :name")
	List<Retention> getRetentionsByIdcardAndName(@Param("idCard")String idCard, @Param("name")String name);
}
