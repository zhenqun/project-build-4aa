package com.ido85.party.sso.security.ldap.application.resources;

import org.springframework.data.ldap.repository.LdapRepository;

import com.ido85.party.sso.security.ldap.application.domain.Account;



public interface AccountRepository extends LdapRepository<Account> {

}
