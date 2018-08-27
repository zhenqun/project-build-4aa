/**
 * 
 */
package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;



/**
 * @author rongxj
 *
 */
@Entity
@Table(name="t_4a_actors")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User implements UserDetails, CredentialsContainer{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -55602628326558195L;
	
	@Id
	private String id;
	
	@Column(name="user_account")
	private String username;
	
	private String password;
	
	private boolean disabled;
	
	@Column(name="email")
	private String email;
	
	@Column(name="name")
	private String name;

	@Column(name="id_card")
	private String idCard;
	
	@Column(name="tele_phone")
	private String telePhone;
	
	@Column(name="create_date")
	private Date createDate;
	
	@Column(name="is_account_expired")
	private boolean accountExpired;//账号是否过期
	@Column(name="is_account_locked")
	private boolean accountLocked;//账号是否锁定
	
	@Column(name="is_pwd_expired")
	private boolean pwdExpired;//密码是否过期
	
	@Column(name="is_enabled")
	private boolean enabled;//用户是否可用
	
	@Column(name="last_login_date")
	private Date lastLoginDate;
	
	@Column(name="hash")
	private String hash;

	@Column(name="is_activation")
	private boolean isActivation;

	@Column(name="activation_date")
	private Date activationDate;

	@Column(name="authorization_code")
	private String authorizationCode;

	@Column(name="logo")
	private String logo;

	@Column(name="create_by")
	private String createBy;

	@ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "r_4a_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<Role> roles;
	
//	@OneToMany(cascade=CascadeType.REMOVE,fetch=FetchType.LAZY)
//	@JoinColumn(name="user_id")
//	private Set<UserClientRel> clients;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>(roles.size());
    	for(Role role : roles) {
    		grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
    	}
        return grantedAuthorities;

	}

	@Override
	public boolean isAccountNonExpired() {
		return !this.accountExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !this.accountLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !this.pwdExpired;
	}


	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void eraseCredentials() {
		password = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelePhone() {
		return telePhone;
	}

	public void setTelePhone(String telePhone) {
		this.telePhone = telePhone;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public boolean isAccountExpired() {
		return accountExpired;
	}

	public void setAccountExpired(boolean accountExpired) {
		this.accountExpired = accountExpired;
	}

	public boolean isAccountLocked() {
		return accountLocked;
	}

	public void setAccountLocked(boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	public boolean isPwdExpired() {
		return pwdExpired;
	}

	public void setPwdExpired(boolean pwdExpired) {
		this.pwdExpired = pwdExpired;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = !disabled;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public boolean isActivation() {
		return isActivation;
	}

	public void setActivation(boolean activation) {
		isActivation = activation;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

//	public Set<UserClientRel> getClients() {
//		return clients;
//	}
//
//	public void setClients(Set<UserClientRel> clients) {
//		this.clients = clients;
//	}


	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
}
