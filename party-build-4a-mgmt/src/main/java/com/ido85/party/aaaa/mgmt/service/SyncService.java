package com.ido85.party.aaaa.mgmt.service;

import com.ido85.party.aaaa.mgmt.ldap.domain.Account;
import com.ido85.party.aaaa.mgmt.ldap.domain.AccountPwdChange;
import com.ido85.party.aaaa.mgmt.ldap.resources.AccountPwdChangeRepository;
import com.ido85.party.aaaa.mgmt.ldap.resources.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.naming.Name;
import java.util.List;


@Service
public class SyncService {

	@Inject
	private AccountRepository repository;

	@Inject
	private AccountPwdChangeRepository accountPwdChangeRepository;

	@Value("${ldap.dn.format}")
	private String dnFormat;
	
	public void save(Account account){
//		Account a = repository.findOne(account.getDn());
//		System.out.println(repositry.findOne(account.getDn()));
//		System.out.println(repositry.exists(account.getDn()));
		repository.save(account);
//		repository.delete(account);
	}
	
	@Transactional
	public void saveAll(List<Account> accountList){
		repository.save(accountList);
	}
	
	public void delete(Account account){
		repository.delete(buildDn(account.getUid(), account.getOu()));
	}

	public void deleteAll(List<Account> accountList){
		repository.delete(accountList);
	}
	
	public Name buildDn(String uid, String ou){
		Name name = LdapNameBuilder.newInstance(String.format(dnFormat, uid, ou)).build();
		return name;
	}
	
	public Account getAccount(String uid,String ou){
		Account account = repository.findOne(buildDn(uid,ou));
		return account;
	}

	public AccountPwdChange getAccountPwdChange(String uid,String ou){
		AccountPwdChange account = accountPwdChangeRepository.findOne(buildDn(uid,ou));
		return account;
	}

	public void saveAccountPwdChange(AccountPwdChange account){
		accountPwdChangeRepository.save(account);
	}

}
