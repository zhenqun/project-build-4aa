package com.ido85.party.aaaa.mgmt.dto.base;

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
	SERVER_PWD("SERVER_PWD"),
	/**
	 * 头像上传路径
	 */
	USERLOGO_EXPORT_PATH("USERLOGO_EXPORT_PATH"),

	/**
	 *  邮件发送者
	 */
	MAIL_FROM("MAIL_FROM"),

	/**
	 *  邮件发送者密码
	 */
	MAIL_PWD("MAIL_PWD"),

	/**
	 *  邮件接收者，逗号隔开
	 */
	MAIL_TO("MAIL_TO"),

	/**
	 *  邮件抄送者，逗号隔开
	 */
	MAIL_COPY("MAIL_COPY"),

	/**
	 *  邮件主题
	 */
	MAIL_SUBJECT("MAIL_SUBJECT");

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
