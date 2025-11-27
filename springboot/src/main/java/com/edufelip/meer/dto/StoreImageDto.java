package com.edufelip.meer.dto;

public record StoreImageDto(
        Integer id,
        String url,
        Integer displayOrder,
        Boolean isCover
) {}
