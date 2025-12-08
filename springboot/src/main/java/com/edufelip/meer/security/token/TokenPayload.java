package com.edufelip.meer.security.token;

import java.util.UUID;
import com.edufelip.meer.core.auth.Role;

public class TokenPayload {
    private final UUID userId;
    private final String email;
    private final String name;
    private final Role role;

    public TokenPayload(UUID userId, String email, String name, Role role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public UUID getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public Role getRole() { return role; }
}
