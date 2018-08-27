/**
 * 
 */
package com.ido85.party.platform.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 客户端异常
 * @author rongxj
 *
 */
@Getter
@Setter
public class ClientException extends Exception{
	
	private int status;
	private String reason;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClientException(){
		
	}
	
	
	public ClientException(int status, String reason){
		this.status = status;
		this.reason = reason;
	}
}
