package com.ido85.party.sso.dto;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;
@Data
public class UserInfoDto {
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
	
	private String username;
	
	private Collection<? extends GrantedAuthority> authorities;
	
	private List<UserRoleDto> roles;
}
