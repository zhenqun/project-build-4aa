package com.ido85.party.aaaa.mgmt.security.authentication.application;

import java.util.List;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.Org;




public interface OrgApplication {
	/***
	 * 查询 当前下的子级
	 * @param id
	 * @return
	 */
	public List<Org> getOrg(String id);
	
	/***
	 * 根据名称查询单位
	 * @param id
	 * @return
	 */
	public List<Org> getOrgByName(String name);
	
	/***
	 * 获取最外层机构单位
	 * @param id
	 * @return
	 */
	public List<Org> getFirstOrg();

	/**
	 * 根据id获取组织机构
	 * @param orgId
	 * @return
	 */
	public Org getOrgById(String orgId);
}
