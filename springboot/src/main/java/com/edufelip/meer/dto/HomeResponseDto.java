package com.edufelip.meer.dto;

import java.util.List;

public record HomeResponseDto(
        List<ThriftStoreDto> featured,
        List<ThriftStoreDto> nearby,
        List<GuideContentDto> content
) {}
