package com.zbw.gitpic.exception;

/**
 * @author zbw
 * @create 2018/3/5 18:38
 */
public class AuthorizedException extends RuntimeException{

    public AuthorizedException() {
    }

    public AuthorizedException(String message) {
        super(message);
    }

    public AuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizedException(Throwable cause) {
        super(cause);
    }
}
