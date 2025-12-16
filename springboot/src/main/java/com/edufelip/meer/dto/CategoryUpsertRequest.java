package com.edufelip.meer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryUpsertRequest(
        @NotBlank @Size(max = 120) String id,
        @NotBlank @Size(max = 120) String nameStringId,
        @NotBlank @Size(max = 240) String imageResId
) {}
