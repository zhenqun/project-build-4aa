package com.ido85.party.sso.security.authentication.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by Administrator on 2017/8/16.
 */
public class UserExpireException  extends AuthenticationException {

    /**
     *
     */
    private static final long serialVersionUID = 1908427981095823456L;

    public UserExpireException(String msg, Throwable t) {
        super(msg, t);
    }

    public UserExpireException(String msg) {
        super(msg);
    }
}
