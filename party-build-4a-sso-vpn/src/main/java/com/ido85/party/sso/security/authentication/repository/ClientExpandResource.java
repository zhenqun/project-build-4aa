package com.ido85.party.sso.security.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sso.security.authentication.domain.ClientExpand;
import com.ido85.party.sso.security.oauth2.domain.PlatformClientDetails;

public interface ClientExpandResource extends JpaRepository<ClientExpand, Long>{

	@Query("select c from ClientExpand c where c.clientId = :id")
	ClientExpand getClientById(@Param("id")String clientId);

}
