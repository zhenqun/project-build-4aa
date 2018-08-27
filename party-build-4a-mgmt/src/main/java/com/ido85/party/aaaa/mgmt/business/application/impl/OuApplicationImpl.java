package com.ido85.party.aaaa.mgmt.business.application.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.ido85.party.aaaa.mgmt.business.application.OuApplication;
import com.ido85.party.aaaa.mgmt.business.domain.Ou;
import com.ido85.party.aaaa.mgmt.business.domain.OuNodeRel;
import com.ido85.party.aaaa.mgmt.business.resources.OuNodeRelResources;
@Named
public class OuApplicationImpl implements OuApplication {

	@Inject
	private OuNodeRelResources ouNodeRelResources;
	
	/**
	 * 获取该组织所在节点
	 */
	public Ou getOuByIdCode(String manageId, String manageCode) {
		Ou ou = null;
		OuNodeRel rel = ouNodeRelResources.getRelBy(manageId);
		if(null != rel){
			ou = rel.getOu();
		}else{
			//如果直接通过id找不到，则需要通过code比对
			
		}
		return ou;
	}

}
