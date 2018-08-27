/**
 * 
 */
package com.ido85.party.sso.security.authentication.domain;


import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author rongxj
 *
 */
@Entity
@Table(name="t_4a_role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role implements Serializable, GrantedAuthority{
	private static final long serialVersionUID = 805249033091589587L;

	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	
	private String description;
	@JsonIgnore
	private String manageId;
	@JsonIgnore
	private String manageName;
	
	private String clientId;
	@JsonIgnore
	@ManyToMany(targetEntity = Resource.class, fetch = FetchType.EAGER)
    @JoinTable(name = "r_4a_role_resource", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "resource_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<Resource> resources;
	
	@ManyToMany(targetEntity = Permission.class, fetch = FetchType.EAGER)
    @JoinTable(name = "r_4a_role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<Permission> permissions;
	/**
	 * The default constructor
	 */
	public Role() {
		
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getAuthority() {
		return this.name;
	}

	public Set<Resource> getResources() {
		return resources;
	}

	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public String getManageId() {
		return manageId;
	}

	public void setManageId(String manageId) {
		this.manageId = manageId;
	}

	public String getManageName() {
		return manageName;
	}

	public void setManageName(String manageName) {
		this.manageName = manageName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}
