package com.ido85.party.aaaa.mgmt.business.application;

import com.ido85.party.aaaa.mgmt.business.domain.Ou;

public interface OuApplication {

	/**
	 * 查询该组织所在节点
	 * @param manageId
	 * @param manageCode
	 * @return
	 */
	Ou getOuByIdCode(String manageId, String manageCode);

}
