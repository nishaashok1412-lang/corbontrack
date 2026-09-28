package com.carbontrack.service;

import com.carbontrack.dto.LeaderboardResponse;
import com.carbontrack.entity.Household;
import com.carbontrack.repository.HouseholdRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class LeaderboardService {

    private final HouseholdRepository householdRepository;
    private final CarbonFootprintService footprintService;

    public LeaderboardService(
            HouseholdRepository householdRepository,
            CarbonFootprintService footprintService) {
        this.householdRepository = householdRepository;
        this.footprintService = footprintService;
    }

    public List<LeaderboardResponse> getLeaderboard(YearMonth month) {
        YearMonth previousMonth = month.minusMonths(1);
        List<LeaderboardResponse> results = new ArrayList<>();

        for (Household household : householdRepository.findAll()) {
            double previous = footprintService
                    .getMonthlyFootprint(household.getId(), previousMonth)
                    .totalCarbonEmissionKg();
            double current = footprintService
                    .getMonthlyFootprint(household.getId(), month)
                    .totalCarbonEmissionKg();

            double reduction = footprintService.calculateReduction(previous, current);

            results.add(new LeaderboardResponse(
                    0, household.getId(), household.getName(),
                    previous, current, reduction
            ));
        }

        results.sort(
                Comparator.comparing(LeaderboardResponse::reductionPercentage)
                        .reversed()
                        .thenComparing(LeaderboardResponse::householdId)
        );

        List<LeaderboardResponse> ranked = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            LeaderboardResponse item = results.get(i);
            ranked.add(new LeaderboardResponse(
                    i + 1,
                    item.householdId(),
                    item.householdName(),
                    item.previousMonthFootprintKg(),
                    item.currentMonthFootprintKg(),
                    item.reductionPercentage()
            ));
        }
        return ranked;
    }
}
