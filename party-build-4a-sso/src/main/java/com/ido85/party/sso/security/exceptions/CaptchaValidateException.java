/**
 * 
 */
package com.ido85.party.sso.security.exceptions;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * 验证码错误异常
 * @author 85ido
 *
 */
public class CaptchaValidateException extends AuthenticationServiceException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7917561994237237137L;

	public CaptchaValidateException(String msg) {
		super(msg);
	}

	public CaptchaValidateException(String msg, Throwable t) {
		super(msg, t);
	}

}
