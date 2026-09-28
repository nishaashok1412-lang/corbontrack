package com.carbontrack.service;

import com.carbontrack.dto.FootprintResponse;
import com.carbontrack.entity.ActivityLog;
import com.carbontrack.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class CarbonFootprintService {

    private final ActivityLogRepository activityRepository;
    private final HouseholdService householdService;

    public CarbonFootprintService(
            ActivityLogRepository activityRepository,
            HouseholdService householdService) {
        this.activityRepository = activityRepository;
        this.householdService = householdService;
    }

    public FootprintResponse getMonthlyFootprint(Long householdId, YearMonth month) {
        householdService.findEntity(householdId);

        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        List<ActivityLog> activities =
                activityRepository.findByHouseholdIdAndActivityDateBetween(
                        householdId, start, end);

        double total = activities.stream()
                .mapToDouble(ActivityLog::getCarbonEmission)
                .sum();

        return new FootprintResponse(householdId, month.toString(), round(total));
    }

    public double calculateReduction(double previousMonth, double currentMonth) {
        if (previousMonth <= 0) {
            return 0.0;
        }
        return round(((previousMonth - currentMonth) / previousMonth) * 100.0);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
