package com.ido85.party.aaaa.mgmt.business.application;

import java.util.List;

import com.ido85.party.aaaa.mgmt.business.dto.ClientRolesDto;

public interface BusinessClientApplication {

	/**
	 * 查询应用以及角色
	 * @return
	 */
	List<ClientRolesDto> getClientsRoles();

}
