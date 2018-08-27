/**
 *
 */
package com.ido85.party.aaa.authentication.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @author rongxj
 *
 */
@Entity
@Table(name="t_4a_actors")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries(value={
		@NamedQuery(name="User.getByUsername", query="select u from User u where u.username = :uname")
})
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

	private boolean disabled;//

	@Column(name="email")
	private String email;

	@Column(name="name")
	private String name;

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

	@Column(name="hash")
	private String hash;

	@Column(name="id_card")
	private String idCard;

	@Column(name="last_login_date")
	private Date lastLoginDate;

	@Column(name="logo")
	private String logo;

	@Column(name="idcard_hash")
	private String idCardHash;

	@Column(name="expire_date")
	private Date expireDate;

	@ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
	@JoinTable(name = "r_4a_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<Role> roles;

	@OneToMany(targetEntity= UserRole.class,fetch = FetchType.EAGER)
	@JoinColumn(name="user_id")
	private Set<UserRole> userroles;
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>(roles.size());
		for(Role role : roles) {
			grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return grantedAuthorities;
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


	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
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

	@Override
	public boolean isEnabled() {
		return !disabled;
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
		this.enabled = enabled;
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

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdCardHash() {
		return idCardHash;
	}

	public void setIdCardHash(String idCardHash) {
		this.idCardHash = idCardHash;
	}

	public Set<UserRole> getUserroles() {
		return userroles;
	}

	public void setUserroles(Set<UserRole> userroles) {
		this.userroles = userroles;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
	public Date getExpireDate(){
		return expireDate;
	}
//	public Set<Role> getRoles() {
//		return roles;
//	}
//
//	public void setRoles(Set<Role> roles) {
//		this.roles = roles;
//	}
}
