/**
 * 
 */
package com.ido85.party.sso.security.authentication.exceptions;


import org.springframework.security.core.AuthenticationException;

/**
 * @author rongxj
 *
 */
public class UserNotEnabledException extends AuthenticationException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1908427981095848898L;

	public UserNotEnabledException(String msg, Throwable t) {
        super(msg, t);
    }

    public UserNotEnabledException(String msg) {
        super(msg);
    }
}