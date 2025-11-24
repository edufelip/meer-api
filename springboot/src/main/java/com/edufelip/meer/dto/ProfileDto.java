package com.edufelip.meer.dto;

import com.edufelip.meer.dto.ThriftStoreDto;

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
