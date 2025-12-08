package com.edufelip.meer.domain.auth;

public class NonAdminUserException extends RuntimeException {
    public NonAdminUserException() {
        super("Admin access only");
    }
}
