package com.edufelip.meer.dto;

import java.time.Instant;

public record AdminUserDto(
        String id,
        String name,
        String email,
        String role,
        Instant createdAt,
        String photoUrl
) {}
