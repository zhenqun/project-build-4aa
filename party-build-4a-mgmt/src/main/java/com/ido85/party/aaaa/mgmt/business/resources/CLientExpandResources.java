package com.ido85.party.aaaa.mgmt.business.resources;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.business.domain.ClientExpand;

import java.util.List;

public interface CLientExpandResources extends JpaRepository<ClientExpand, Long>{

	@Query("select e from ClientExpand e where e.clientId = :clientId")
	ClientExpand getClientById(@Param("clientId")String clientId);

	@Query("select e from ClientExpand e where e.clientId in :ids ORDER BY e.order")
	List<ClientExpand> getClientByIds(@Param("ids") List<String> clientIds);
}
