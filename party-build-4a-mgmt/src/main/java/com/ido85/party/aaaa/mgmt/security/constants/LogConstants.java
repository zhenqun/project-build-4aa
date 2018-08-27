package com.ido85.party.aaaa.mgmt.security.constants;

public class LogConstants{
	
	//登录
	public static final String LOGIN_SUCCESS = "1000";
	
	public static final String LOGIN_FAIL = "1001";
	
	//注册
	public static final String REGISTER_SUCCESS = "1100";
	
	public static final String REGISTER_FAIL = "1101";
	
	//审计员相关
	public static final String ADD_AUDITOR = "1200";
	public static final String ADD_AUDITOR_NAME = "开通审计员";
	
	public static final String MOD_AUDITOR_STATE = "1203";
	public static final String MOD_AUDITOR_STATE_NAME = "禁用/启用审计员";
	
	public static final String CANCEL_AUDITOR = "1206";
	public static final String CANCEL_AUDITOR_NAME = "注销审计员";
	
	public static final String EXPORT_AUDITOR_CODE = "1209";
	public static final String EXPORT_AUDITOR_CODE_NAME = "导出审计员授权码";
	
	public static final String SET_AUDITOR_MANAGE = "1212";
	public static final String SET_AUDITOR_MANAGE_NAME = "设置审计员管理范围";
	
	//安全员相关
	public static final String ADD_SECURITYUSER = "1202";
	public static final String ADD_SECURITYUSER_NAME = "开通安全员";
	
	public static final String MOD_SECURITYUSER_STATE = "1205";
	public static final String MOD_SECURITYUSER_STATE_NAME = "禁用/启用安全员";
	
	public static final String CANCEL_SECURITYUSER = "1208";
	public static final String CANCEL_SECURITYUSER_NAME = "注销安全员";
	
	public static final String EXPORT_SECURITYUSER_CODE = "1211";
	public static final String EXPORT_SECURITYUSER_CODE_NAME = "导出安全员授权码";
	
	public static final String SET_SECURITYUSER_MANAGE = "1214";
	public static final String SET_SECURITYUSER_MANAGE_NAME = "设置安全员管理范围";
	
	//业务管理员相关
	public static final String ADD_BUSINESSUSER = "1201";
	public static final String ADD_BUSINESSUSER_NAME = "开通业务管理员";
	
	public static final String MOD_BUSINESSUSER_STATE = "1204";
	public static final String MOD_BUSINESSUSER_STATE_NAME = "禁用/启用业务管理员";
	
	public static final String CANCEL_BUSINESSUSER = "1207";
	public static final String CANCEL_BUSINESSUSER_NAME = "注销业务管理员";
	
	public static final String EXPORT_BUSINESSUSER_CODE = "1210";
	public static final String EXPORT_BUSINESSUSER_CODE_NAME = "导出业务管理员授权码";
	
	public static final String SET_BUSINESSUSER_MANAGE = "1213";
	public static final String SET_BUSINESSUSER_MANAGE_NAME = "设置业务管理员管理范围";
	
	public static final String GRANT_BUSINESSUSER = "1215";
	public static final String GRANT_BUSINESSUSER_NAME = "授权业务管理员";
}
