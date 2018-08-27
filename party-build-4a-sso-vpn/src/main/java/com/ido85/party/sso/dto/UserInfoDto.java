package com.ido85.party.sso.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import com.ido85.party.sso.security.authentication.domain.Role;
import com.ido85.party.sso.security.authentication.domain.UserRole;

import lombok.Data;
@Data
public class UserInfoDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6306782458011634203L;

	/**
	 * 个人信息dto
	 */
	
	private String userId;
	
	private String name;
	
	private String telephone;
	
	private String logo;
	
	private String lastLoginDate;
	
	private Date lastLoginTime;
	
	private String hash;
	
//	private Collection<? extends GrantedAuthority> authorities;
	
	private List<RoleDto> roles;

}
