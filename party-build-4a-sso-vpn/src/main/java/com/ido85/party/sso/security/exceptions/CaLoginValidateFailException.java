/**
 * 
 */
package com.ido85.party.sso.security.exceptions;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * 必须使用ca登录失败异常
 * @author 85ido
 *
 */
public class CaLoginValidateFailException extends AuthenticationServiceException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7917561994237237137L;

	public CaLoginValidateFailException(String msg) {
		super(msg);
	}

	public CaLoginValidateFailException(String msg, Throwable t) {
		super(msg, t);
	}

}
