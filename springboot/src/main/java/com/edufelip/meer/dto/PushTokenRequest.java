package com.edufelip.meer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PushTokenRequest(
    @NotBlank(message = "deviceId is required") @Size(max = 255) String deviceId,
    @NotBlank(message = "fcmToken is required") @Size(max = 4096) String fcmToken,
    @NotBlank(message = "platform is required") @Size(max = 32) String platform,
    @Size(max = 64) String appVersion,
    @NotBlank(message = "environment is required") @Size(max = 32) String environment) {}
