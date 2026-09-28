package com.carbontrack.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ParticipationResponse(
        Long participationId,
        Long householdId,
        Long challengeId,
        String challengeName,
        LocalDate startDate,
        LocalDate endDate,
        Double targetReductionPercentage,
        LocalDateTime joinedAt,
        Double previousMonthFootprintKg,
        Double currentMonthFootprintKg,
        Double reductionPercentage,
        Boolean targetAchieved
) {}
