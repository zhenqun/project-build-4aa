/**
 * 
 */
package com.ido85.party.sso.security.oauth2.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import com.google.common.collect.Sets;

/**
 * @author rongxj
 *
 */
@Entity
@Table(name = "oauth_client_details")
public class PlatformClientDetails extends BaseClientDetails implements
		ClientDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 101639926601960704L;

	@Id
	@Column(name = "client_id")
	private String clientId;

	@Column(name = "access_token_validity")
	private Integer accessTokenValidity;
	@Column(name = "additional_information")
	private String additionalInformation;
	@Column(name = "authorities")
	private String authorities;
	@Column(name = "authorized_grant_types")
	private String authorizedGrantTypes;

	@Column(name = "client_secret")
	private String clientSecret;
//	@Column(name = "create_time")
//	private Date createTime = new Date();

	@Column(name = "refresh_token_validity")
	private Integer refreshTokenValidity;
	@Column(name = "resource_ids")
	private String resourceIds;
	@Column(name = "scope")
	private String scope;
//	@Column(name = "trusted")
//	private Boolean trusted;
	@Column(name = "web_server_redirect_uri")
	private String webServerRedirectUri;
	@Column(name = "autoapprove")
	private Boolean autoapprove;

	@Column(name="client_name")
	private String clientName;
	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public Set<String> getResourceIds() {
		return toSet(resourceIds);
	}

	@Override
	public String getClientSecret() {
		// TODO Auto-generated method stub
		return clientSecret;
	}

	@Override
	public Set<String> getScope() {
		return toSet(scope);
	}

	@Override
	public Set<String> getAuthorizedGrantTypes() {
		return toSet(authorizedGrantTypes);
	}

	@Override
	public Set<String> getRegisteredRedirectUri() {
		return toSet(webServerRedirectUri);
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> res = Sets.newHashSet();
		if (authorities != null) {
			toSet(authorities).forEach((auth) -> {
				GrantedAuthority gauth = new SimpleGrantedAuthority(auth);
				res.add(gauth);
			});

		}
		return res;
	}

	@Override
	public Integer getAccessTokenValiditySeconds() {
		return accessTokenValidity;
	}

	@Override
	public Integer getRefreshTokenValiditySeconds() {
		return refreshTokenValidity;
	}

	@Override
	public boolean isAutoApprove(String scope) {
		return autoapprove;
	}

	private static Set<String> toSet(String source) {
		Set<String> res = Sets.newHashSet();
		if (source != null) {
			res.addAll(Arrays.asList(source.split(",")));
		}
		return res;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

}
