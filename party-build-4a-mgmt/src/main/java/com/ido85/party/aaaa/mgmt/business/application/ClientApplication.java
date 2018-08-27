package com.ido85.party.aaaa.mgmt.business.application;

import java.util.List;

public interface ClientApplication {

	/**
	 * 根据应用id和管理id获取所有子节点分级id
	 * @param clientId
	 * @param manageId
	 * @return
	 */
	List<String> getChildManageIds(String clientId, String manageId);

}
