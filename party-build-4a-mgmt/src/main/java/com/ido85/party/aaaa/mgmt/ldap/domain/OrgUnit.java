/**
 * 
 */
package com.ido85.party.aaaa.mgmt.ldap.domain;

import java.io.Serializable;

import javax.naming.Name;

import lombok.Data;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Attribute.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * 组织节点
 * @author rongxj
 *
 */
@Data
@Entry(objectClasses = { "organizationalUnit", "top"})
public class OrgUnit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6928634777661808091L;

	@JsonIgnore
	@Id
	private Name dn;
	
	/**
	 * 用户组织
	 */
	@DnAttribute(value = "ou", index = 0)
	@Attribute(name = "ou", type = Type.STRING)
	private String ou;
	
}
