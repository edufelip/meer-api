package com.edufelip.meer.dto;

import java.util.List;

public record PageResponse<T>(List<T> items, int page, boolean hasNext) {}
