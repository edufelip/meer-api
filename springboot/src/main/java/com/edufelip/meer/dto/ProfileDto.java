package com.edufelip.meer.dto;

public record ProfileDto(
        Integer id,
        String name,
        String email,
        String avatarUrl,
        String bio,
        boolean notifyNewStores,
        boolean notifyPromos,
        ThriftStoreDto ownedThriftStore
) {}
