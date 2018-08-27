/**
 * 
 */
package com.ido85.party.aaaa.mgmt.internet.domain;


import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author rongxj
 *
 */
@Entity
@Table(name="t_4a_role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class InternetRole implements Serializable, GrantedAuthority{
	private static final long serialVersionUID = 805249033091589587L;

	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	
	private String description;
	
	@ManyToMany(targetEntity = InternetResource.class, fetch = FetchType.EAGER)
    @JoinTable(name = "r_4a_role_resource", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "resource_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<InternetResource> resources;
	
	/**
	 * The default constructor
	 */
	public InternetRole() {
		
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
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
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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

	/**
	 * @param resources the resources to set
	 *//*
	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}
	
	*//**
	 * @return the resources
	 *//*
	public Set<Resource> getResources() {
		return resources;
	}*/

}
