package com.ido85.party.sso.security.authentication.application.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.ido85.party.sso.security.authentication.application.OrgApplication;
import com.ido85.party.sso.security.authentication.domain.Org;
import com.ido85.party.sso.security.authentication.repository.OrgResources;
@Named
public class OrgApplicationImpl implements OrgApplication{
	@Inject
	private OrgResources orgRes;
	@Override
	public List<Org> getOrg(String id) {
		return orgRes.getOrg(id);
	}
	@Override
	public List<Org> getOrgByName(String name) {
		return orgRes.getOrgByName(name);
	}
	@Override
	public List<Org> getFirstOrg() {
		return orgRes.getFirstOrg();
	}
	@Override
	public Org getOrgById(String orgId) {
		return orgRes.getOrgById(orgId);
	}


}
