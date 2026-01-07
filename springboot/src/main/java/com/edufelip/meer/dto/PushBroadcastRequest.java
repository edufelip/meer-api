package com.edufelip.meer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PushBroadcastRequest(
    @NotBlank(message = "environment is required") @Size(max = 32) String environment,
    @NotBlank(message = "audience is required") @Size(max = 32) String audience,
    @NotBlank(message = "title is required") @Size(max = 120) String title,
    @NotBlank(message = "body is required") @Size(max = 2000) String body,
    @NotBlank(message = "type is required") @Size(max = 64) String type,
    @NotBlank(message = "id is required") @Size(max = 64) String id) {}
