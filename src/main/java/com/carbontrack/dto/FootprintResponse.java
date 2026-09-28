package com.carbontrack.dto;

public record FootprintResponse(
        Long householdId,
        String month,
        Double totalCarbonEmissionKg
) {}
