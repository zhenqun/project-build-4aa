/**
 * 
 */
package com.ido85.party.aaaa.mgmt.service;

import javax.inject.Inject;
import javax.naming.Name;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import com.ido85.party.aaaa.mgmt.ldap.domain.OrgUnit;
import com.ido85.party.aaaa.mgmt.ldap.resources.OrgUnitRepository;


/**
 * @author IBM
 *
 */
@Service
public class OrgUnitService {

	
	@Value("${ldap.dn.format}")
	private String dnFormat;
	
	@Inject
	private OrgUnitRepository repository;
	
	public boolean orgUnitExits(String org){
		return repository.exists(buildDn(org));
	}
	
	public Iterable<OrgUnit> getOrgUnits(){
		return repository.findAll();
	}
	
	public void newOrg(OrgUnit org){
		repository.save(org);
	}
	
	public Name buildDn(String ou){
		Name name = LdapNameBuilder.newInstance(ou).build();
		return name;
	}
}
