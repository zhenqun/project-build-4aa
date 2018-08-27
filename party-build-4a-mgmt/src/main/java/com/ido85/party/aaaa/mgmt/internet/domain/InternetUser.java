/**
 *
 */
package com.ido85.party.aaaa.mgmt.internet.domain;

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
		@NamedQuery(name="InternetUser.getByUsername", query="select u from InternetUser u where u.username = :uname")
})
public class InternetUser implements UserDetails, CredentialsContainer{


	/**
	 *
	 */
	private static final long serialVersionUID = -55602628326558195L;

	@Id
	private String id;

	@Column(name="USER_ACCOUNT")
	private String username;

//	@Column(name="CATEGORY")
//	private String category;//用户类型

	private String password;

	private boolean disabled;

	@Column(name="EMAIL")
	private String email;

	@Column(name="NAME")
	private String name;

	@Column(name="TELE_PHONE")
	private String telePhone;

//	@Column(name="identity")
//	private String identity;

	@Column(name="CREATE_DATE")
	private Date createDate;

//	@Column(name="EMPLOYEE_ID")
//	private Long employeeId;

	@Column(name="IS_ACCOUNT_EXPIRED")
	private boolean accountExpired;//账号是否过期
	@Column(name="IS_ACCOUNT_LOCKED")
	private boolean accountLocked;//账号是否锁定

	@Column(name="IS_PWD_EXPIRED")
	private boolean pwdExpired;//密码是否过期

	@Column(name="IS_ENABLED")
	private boolean enabled;//用户是否可用

	@Column(name="id_card")
	private String idCard;

//	@Column(name="org_id")
//	private String orgId;

	@Column(name="idcard_hash")
	private String idCardHash;

	@Column(name="hash")
	private String hash;

	@Column(name="update_date")
	private Date updateDate;

	@ManyToMany(targetEntity = InternetRole.class, fetch = FetchType.EAGER)
	@JoinTable(name = "r_4a_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<InternetRole> roles;
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

//	public String getCategory() {
//		return category;
//	}
//
//	public void setCategory(String category) {
//		this.category = category;
//	}



	public Set<InternetRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<InternetRole> roles) {
		this.roles = roles;
	}

//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		for(InternetRole role : roles) {
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

//	public String getIdentity() {
//		return identity;
//	}
//
//	public void setIdentity(String identity) {
//		this.identity = identity;
//	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

//	public Long getEmployeeId() {
//		return employeeId;
//	}
//
//	public void setEmployeeId(Long employeeId) {
//		this.employeeId = employeeId;
//	}

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

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

//	public String getOrgId() {
//		return orgId;
//	}
//
//	public void setOrgId(String orgId) {
//		this.orgId = orgId;
//	}

	public boolean isEnabled() {
		return !disabled;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getIdCardHash() {
		return idCardHash;
	}

	public void setIdCardHash(String idCardHash) {
		this.idCardHash = idCardHash;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
