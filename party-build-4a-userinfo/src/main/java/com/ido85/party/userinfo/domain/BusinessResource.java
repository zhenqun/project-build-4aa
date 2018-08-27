/**
 * 
 */
package com.ido85.party.userinfo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author rongxj
 */
@Entity
@Table(name="t_4a_resource")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BusinessResource implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3885604242039083175L;

	@Id
    @GeneratedValue
	private Integer id;
	
	private String type;
	
	private String value;
	
	@ManyToMany(mappedBy = "resources", targetEntity = BusinessRole.class, fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<BusinessRole> roles;
	
	/**
	 * The default constructor
	 */
	public BusinessResource() {
		
	}
	
    /**
     * Get role authorities as string
     * 
     * @return
     */
    @Transient
	public String getRoleAuthorities() {
    	List<String> roleAuthorities = new ArrayList<String>();
    	for(BusinessRole role : roles) {
    		roleAuthorities.add(role.getName());
    	}
        return StringUtils.join(roleAuthorities, ",");
    }

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the roles
	 */
	public Set<BusinessRole> getRoles() {
		return roles;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(Set<BusinessRole> roles) {
		this.roles = roles;
	}

}
