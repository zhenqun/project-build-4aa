/**
 * 
 */
package com.ido85.party.aaaa.mgmt.security.authentication.exceptions;


import org.springframework.security.core.AuthenticationException;

/**
 * @author rongxj
 *
 */
public class UserPasswordExpiredException extends AuthenticationException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1908427981095848898L;

	public UserPasswordExpiredException(String msg, Throwable t) {
        super(msg, t);
    }

    public UserPasswordExpiredException(String msg) {
        super(msg);
    }
}