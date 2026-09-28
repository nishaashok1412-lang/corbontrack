package com.carbontrack.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        Integer status,
        String error,
        Map<String, String> fields
) {}
