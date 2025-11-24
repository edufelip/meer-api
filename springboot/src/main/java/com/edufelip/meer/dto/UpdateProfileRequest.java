package com.edufelip.meer.dto;

public record UpdateProfileRequest(
        String name,
        String avatarUrl,
        String bio,
        Boolean notifyNewStores,
        Boolean notifyPromos
) {}
