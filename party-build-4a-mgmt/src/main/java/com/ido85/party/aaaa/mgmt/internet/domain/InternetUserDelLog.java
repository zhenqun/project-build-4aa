/**
 *
 */
package com.ido85.party.aaaa.mgmt.internet.domain;

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
@Table(name="t_4a_actors_del_log")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class InternetUserDelLog implements UserDetails, CredentialsContainer{


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

	@Column(name="CREATE_DATE")
	private Date createDate;

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

	@Column(name="idcard_hash")
	private String idCardHash;

	@Column(name="hash")
	private String hash;

	@Column(name="update_date")
	private Date updateDate;
	@Column(name="del_date")
	private Date delDate;

	public Date getDelDate() {
		return delDate;
	}

	public void setDelDate(Date delDate) {
		this.delDate = delDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
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

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	@Override
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
