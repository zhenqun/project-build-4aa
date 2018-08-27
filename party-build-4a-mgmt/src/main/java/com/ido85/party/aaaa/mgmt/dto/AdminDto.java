package com.ido85.party.aaaa.mgmt.dto;

public class AdminDto {
	
    private String username;//用户名
	
	private String name;//姓名
	
	private String identity;//身份
	
	private String idCard;//身份证号
	
	private String orgName;//组织机构名称
	
	private String phone;//联系方式
	
	private String registerDate;//注册时间
	
	private String applicationNames;//应用范围
	
	private String lastLoginDate;
	
	private String applicationIds;
	
	private Integer status;//使用状态
	
	private Long userId;//id

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}

	public String getApplicationNames() {
		return applicationNames;
	}

	public void setApplicationNames(String applicationNames) {
		this.applicationNames = applicationNames;
	}

	public String getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(String lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getApplicationIds() {
		return applicationIds;
	}

	public void setApplicationIds(String applicationIds) {
		this.applicationIds = applicationIds;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
