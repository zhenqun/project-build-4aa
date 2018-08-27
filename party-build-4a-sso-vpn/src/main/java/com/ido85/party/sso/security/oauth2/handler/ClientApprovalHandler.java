/**
 * 
 */
package com.ido85.party.sso.security.oauth2.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
/**
 * @author rongxj
 *
 */
public class ClientApprovalHandler extends TokenStoreUserApprovalHandler {
	private ClientDetailsService clientDetailsService;
	@Override
	public boolean isApproved(AuthorizationRequest authorizationRequest,
			Authentication userAuthentication) {
		if(super.isApproved(authorizationRequest, userAuthentication)) {
			return true;
		}
		if (!userAuthentication.isAuthenticated()) {
            return false;
        }
		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(authorizationRequest.getClientId());
//		return clientDetails != null && clientDetails.getTrusted();
		return clientDetails != null && clientDetails.isScoped();
	}
	public void setClientDetailsService(ClientDetailsService clientDetailsService) {
		this.clientDetailsService = clientDetailsService;
	}
}
