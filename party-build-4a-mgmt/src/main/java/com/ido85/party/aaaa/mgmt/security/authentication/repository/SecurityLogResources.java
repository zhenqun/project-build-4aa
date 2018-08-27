package com.ido85.party.aaaa.mgmt.security.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.SecurityLog;

public interface SecurityLogResources extends JpaRepository<SecurityLog, Long>{

}
