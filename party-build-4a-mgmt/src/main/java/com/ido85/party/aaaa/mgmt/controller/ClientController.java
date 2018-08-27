package com.ido85.party.aaaa.mgmt.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ido85.party.aaaa.mgmt.business.domain.PlatformClientDetails;
import com.ido85.party.aaaa.mgmt.business.resources.CLientResources;
import com.ido85.party.aaaa.mgmt.dto.client.ClientDto;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;

@RestController
public class ClientController {

	@Inject
	private CLientResources clientRsources;
	
	/**
	 * 查询所有应用
	 * @return
	 */
	@RequestMapping(path="/common/clientQuery",method={RequestMethod.POST})
	@ResponseBody
	public List<ClientDto> clientQuery(){
		List<ClientDto> dtoList = new ArrayList<>();
		ClientDto dto = null;
		List<PlatformClientDetails> clientList = clientRsources.getAllClient();
		if(ListUntils.isNotNull(clientList)){
			for(PlatformClientDetails client:clientList){
				dto = new ClientDto();
				dto.setClassificationUrl(client.getLevelUrl());
				dto.setClientName(client.getClientName());
				dtoList.add(dto);
			}
		}
		return dtoList;
	}
}
