package com.ido85.party.sso.ws.domain;

import java.io.Serializable;


public class LiensceObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String KHID;
	
	public String YHID;
	
	public String ZHID;
	
	public String PASSWORD;
	
	public String IP;
	
	public String FWID;
	
	public byte[] file;
	
	public String getKHID() {
		return KHID;
	}

	public void setKHID(String KHID) {
		this.KHID = KHID;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getYHID() {
		return YHID;
	}

	public void setYHID(String YHID) {
		this.YHID = YHID;
	}

	public String getZHID() {
		return ZHID;
	}

	public void setZHID(String ZHID) {
		this.ZHID = ZHID;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String PASSWORD) {
		this.PASSWORD = PASSWORD;
	}
	
	public String getIP() {
		return IP;
	}

	public void setIP(String IP) {
		this.IP = IP;
	}
	
	public String getFWID() {
		return FWID;
	}

	public void setFWID(String FWID) {
		this.FWID = FWID;
	}
}
