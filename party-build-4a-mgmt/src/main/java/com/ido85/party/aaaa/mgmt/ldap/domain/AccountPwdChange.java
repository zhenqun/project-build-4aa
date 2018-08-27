package com.ido85.party.aaaa.mgmt.ldap.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Attribute.Type;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

/**
 * @author rongxj <br/>
 * <p>
 * objectClasses: 对象类,由多个attributetype(属性类型)组成,每个实体属于一个或多个对象类 <br/>
 * base: basedn 基础DN，根节点
 * 此实体类专用于更换节点使用!!!!
 * </p>
 */
@Data
@Entry(objectClasses = { "person", "organizationalPerson", "inetOrgPerson", "top",
		"posixAccount", "shadowAccount" })
//		,base = "dc=inspur,dc=com")
public class AccountPwdChange {

	/**
	 * Distinguished Name<br/>
	 * exp: uid=ldapuser4,ou=People,dc=inspur,dc=com
	 */
	@JsonIgnore
	@Id
	private Name dn;
	
	/**
	 * 用户组织
	 */
	@DnAttribute(value = "ou", index = 0)
	private String ou;

	/**
	 * uid
	 */
	@Attribute(name = "uid", type = Type.STRING)
	@DnAttribute(value="uid", index=1)
	private String uid;
	

	/**
	 * commonname,
	 */
	@Attribute(name = "cn", type = Type.STRING)
	private String name;

	/**
	 * 姓氏
	 */
	@Attribute(name = "sn", type = Type.STRING)
	private String surname;

	/**
	 * mail
	 */
	@Attribute(name = "mail", type = Type.STRING)
	private String mail;
	
	/**
	 * crypt 加密
	 */
	@Attribute(name = "userPassword", type = Type.BINARY)
	private byte[] userPassword;
	
	/**
	 * 最后一次修改间隔（1970年1月1日开始），单位 天
	 */
	@Attribute(name = "shadowLastChange")
	private int shadowLastChange;
	
	/**
	 * 从上一次修改后，多长时间内不允许修改密码
	 */
	@Attribute(name = "shadowMin")
	private int shadowMin;
	
	/**
	 * 从上一次修改后，多长时间过期
	 */
	@Attribute(name = "shadowMax")
	private int shadowMax;
	
	/**
	 * 提前多少天提醒用户密码过期
	 */
	@Attribute(name = "shadowWarning")
	private int shadowWarning;
	
	/**
	 * 用户ID
	 */
	@Attribute(name = "uidNumber")
	private int uidNumber;
	
	/**
	 * 用户组ID
	 */
	@Attribute(name = "gidNumber")
	private int gidNumber;
	
	/**
	 * 用户主目录<br/>
	 *  /home/uid
	 */
	@Attribute(name = "homeDirectory")
	private String homeDirectory;
}
