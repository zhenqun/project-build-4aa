package com.ido85.party.sso.security.constants;

public enum ConfigEnum {
	/**
	 * 导出服务器ip
	 */
	SERVER("SERVER"),
	/**
	 * 导出服务器post
	 */
	PORT("PORT"),
	/**
	 * 临时导出文件夹
	 */
	TEMP_EXPORT_PATH("TEMP_EXPORT_PATH"),
	/**
	 * 导出文件目录
	 */
	EXPORT_PATH("EXPORT_PATH"),
	/**
	 * FTP用户
	 */
	SERVER_ACCOUNT("SERVER_ACCOUNT"),
	/**
	 * ftp密码
	 */
	SERVER_PWD("SERVER_PWD");



	private String code;

	ConfigEnum(String value){
		this.code = value;
	}

	@Override
	public String toString() {
		return code;
	}

	public String getCode(){
		return code;
	}
}
