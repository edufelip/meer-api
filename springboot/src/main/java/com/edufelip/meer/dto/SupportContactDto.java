package com.edufelip.meer.dto;

import java.time.Instant;

public record SupportContactDto(
        Integer id,
        String name,
        String email,
        String message,
        Instant createdAt
) {}
