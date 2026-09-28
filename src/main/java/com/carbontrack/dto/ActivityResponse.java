package com.carbontrack.dto;

import com.carbontrack.entity.ActivityType;

import java.time.LocalDate;

public record ActivityResponse(
        Long id,
        Long householdId,
        LocalDate activityDate,
        ActivityType activityType,
        Double quantity,
        Double carbonEmissionKg
) {}
