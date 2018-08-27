/**
 * 
 */
package com.ido85.party.sso.security.oauth2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ido85.party.sso.security.oauth2.domain.PlatformClientDetails;

/**
 * oauth客户端操作资源库
 * @author rongxj
 *
 */
public interface ClientRepository extends JpaRepository<PlatformClientDetails, String>{

}
