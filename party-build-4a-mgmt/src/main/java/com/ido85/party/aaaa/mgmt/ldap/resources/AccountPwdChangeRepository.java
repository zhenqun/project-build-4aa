package com.ido85.party.aaaa.mgmt.ldap.resources;

import com.ido85.party.aaaa.mgmt.ldap.domain.Account;
import com.ido85.party.aaaa.mgmt.ldap.domain.AccountPwdChange;
import org.springframework.data.ldap.repository.LdapRepository;


public interface AccountPwdChangeRepository extends LdapRepository<AccountPwdChange> {

}
