package com.ido85.party.aaaa.mgmt.business.application.impl;

import com.ido85.party.aaaa.mgmt.business.application.ClientApplication;
import com.ido85.party.aaaa.mgmt.business.domain.ClientExpand;
import com.ido85.party.aaaa.mgmt.business.resources.CLientExpandResources;
import com.ido85.party.aaaa.mgmt.dto.OrganizationDto;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Named
public class ClientApplicationImpl implements ClientApplication {

	@Inject
	private CLientExpandResources clientResources;

	@Inject
	private RestTemplate restTemplate;
	
	/**
	 * 根据管理id获取所有子节点分级id
	 * @param clientId
	 * @param manageId
	 * @return
	 */
	public List<String> getChildManageIds(String clientId, String manageId) {
		ClientExpand ce = clientResources.getClientById(clientId);
		String levelUrl = null;
		String isEureka = null;
		if(null != ce){
			levelUrl = ce.getLevelUrl();
			isEureka = ce.getIsEureka();
		}
		//获取子节点分级id
		List<String> manageIds  = getManageIds(levelUrl,manageId,isEureka);
		return manageIds;
	}

	/**
	 * 获取子节点分级id
	 * @param levelUrl
	 * @param manageId
	 * @return
	 */
	private List<String> getManageIds(String levelUrl, String manageId, String isEureka) {
		List<String> manageIds = new ArrayList<String>();
		List<OrganizationDto> orgDtoList = new ArrayList<OrganizationDto>();
		String url = "";
		if("1".equals(isEureka)){
			if(!StringUtils.isNull(manageId)){
				url = levelUrl+manageId;
				orgDtoList = Arrays.asList(restTemplate.getForEntity(url, OrganizationDto[].class).getBody());
			}
			if(ListUntils.isNotNull(orgDtoList)){
				for(OrganizationDto dto:orgDtoList){
					manageIds.add(dto.getOrgId());
				}
			}
		}

		if("0".equals(isEureka)){
			RestTemplate restTemplate2 = new RestTemplate();
			if(!StringUtils.isNull(manageId)){
				url = levelUrl+manageId;
				orgDtoList = Arrays.asList(restTemplate2.getForEntity(url, OrganizationDto[].class).getBody());
			}
			if(ListUntils.isNotNull(orgDtoList)){
				for(OrganizationDto dto:orgDtoList){
					manageIds.add(dto.getOrgId());
				}
			}
		}
		return manageIds;
	}

}
