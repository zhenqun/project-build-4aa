package com.ido85.party.sso.security.authentication.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.ido85.party.sso.security.authentication.domain.ClientUserRel;

public interface ClientUserRelResources extends JpaRepository<ClientUserRel, Long> {

}
