package com.edufelip.meer.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 120) String name,
        @Size(max = 2048) String avatarUrl,
        @Size(max = 200) String bio,
        Boolean notifyNewStores,
        Boolean notifyPromos
) {}
