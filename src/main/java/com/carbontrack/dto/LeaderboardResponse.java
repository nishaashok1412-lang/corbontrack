package com.carbontrack.dto;

public record LeaderboardResponse(
        Integer rank,
        Long householdId,
        String householdName,
        Double previousMonthFootprintKg,
        Double currentMonthFootprintKg,
        Double reductionPercentage
) {}
