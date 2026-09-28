package com.carbontrack.dto;

import java.time.LocalDate;

public record ChallengeResponse(
        Long id,
        String name,
        String description,
        Double targetReductionPercentage,
        LocalDate startDate,
        LocalDate endDate
) {}
