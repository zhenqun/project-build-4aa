package com.ido85.party.sso.ws.domain;


import java.io.Serializable;

public class FileObjectSetPhone implements Serializable {
	
	//��Ȩ�ļ�����
	private String license=null;
	//�ϴ�������excel�ļ�
	private byte[] file=null;
	//������Ϣ
	private String info=null;
	//�ļ�����:xls
	private String type=null;
	
	public FileObjectSetPhone() {
		super();
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}


	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
