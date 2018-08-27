/**
 * 
 */
package com.ido85.party.sso.security.oauth2.provider;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import com.ido85.party.sso.security.oauth2.repository.ClientRepository;

/**
 * 客户端service
 * @author rongxj
 *
 */
@Named
public class PlatformClientDetailService implements ClientDetailsService{

	@Inject
	private ClientRepository clientRepository;
	
	@Override
	public ClientDetails loadClientByClientId(String clientId)
			throws ClientRegistrationException {
		return clientRepository.findOne(clientId);
	}

}
