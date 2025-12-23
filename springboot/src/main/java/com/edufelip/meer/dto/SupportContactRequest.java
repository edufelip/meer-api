package com.edufelip.meer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupportContactRequest(
    @NotBlank @Size(max = 120) String name,
    @NotBlank @Email @Size(max = 320) String email,
    @NotBlank @Size(max = 2000) String message) {}
