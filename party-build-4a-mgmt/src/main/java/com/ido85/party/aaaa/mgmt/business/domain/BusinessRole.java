/**
 * 
 */
package com.ido85.party.aaaa.mgmt.business.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author rongxj
 *
 */
@Entity
@Table(name="t_4a_role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BusinessRole implements Serializable, GrantedAuthority{
	private static final long serialVersionUID = 805249033091589587L;

	@Id
	@Column(name="id")
	private Long id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="description")
	private String description;
	
	@Column(name="manage_id")
	private String manageId;
	
	@Column(name="manage_name")
	private String manageName;
	
	@Column(name="client_id")
	private String clientId;
	
	@Column(name="create_date")
	private Date createDate;
	
	@Column(name="create_by")
	private String createBy;
	
	@Column(name="update_by")
	private String updateBy;
	
	@Column(name="update_date")
	private Date updateDate;

	@Column(name="is_common")
	private boolean isCommon;

	@Column(name="detail")
	private String detail;

	@ManyToMany(targetEntity = BusinessResource.class, fetch = FetchType.EAGER)
    @JoinTable(name = "r_4a_role_resource", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "resource_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<BusinessResource> resources;
	
	@ManyToMany(targetEntity = Permission.class, fetch = FetchType.EAGER)
    @JoinTable(name = "r_4a_role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<Permission> permissions;
	
	/**
	 * The default constructor
	 */
	public BusinessRole() {
		
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public boolean isCommon() {
		return isCommon;
	}

	public void setCommon(boolean common) {
		isCommon = common;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
	/**
	 * @param resources the resources to set
	 */
	/*
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
