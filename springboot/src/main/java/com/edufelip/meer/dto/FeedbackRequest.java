package com.edufelip.meer.dto;

import jakarta.validation.constraints.Size;

public record FeedbackRequest(Integer score, @Size(max = 2000) String body) {}
