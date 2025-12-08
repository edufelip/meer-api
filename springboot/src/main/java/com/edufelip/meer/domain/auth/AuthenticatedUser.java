package com.edufelip.meer.domain.auth;

public class AuthenticatedUser {
    private final java.util.UUID id;
    private final String name;
    private final String email;
    private final com.edufelip.meer.core.auth.Role role;

    public AuthenticatedUser(java.util.UUID id, String name, String email, com.edufelip.meer.core.auth.Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public java.util.UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public com.edufelip.meer.core.auth.Role getRole() { return role; }
}
