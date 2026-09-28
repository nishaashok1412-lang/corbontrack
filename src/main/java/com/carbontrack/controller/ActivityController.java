package com.carbontrack.controller;

import com.carbontrack.dto.ActivityResponse;
import com.carbontrack.dto.CreateActivityRequest;
import com.carbontrack.dto.FootprintResponse;
import com.carbontrack.service.ActivityService;
import com.carbontrack.service.CarbonFootprintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/households")
public class ActivityController {

    private final ActivityService activityService;
    private final CarbonFootprintService footprintService;

    public ActivityController(
            ActivityService activityService,
            CarbonFootprintService footprintService) {
        this.activityService = activityService;
        this.footprintService = footprintService;
    }

    @PostMapping("/{householdId}/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityResponse create(
            @PathVariable Long householdId,
            @Valid @RequestBody CreateActivityRequest request) {
        return activityService.create(householdId, request);
    }

    @GetMapping("/{householdId}/activities")
    public List<ActivityResponse> getActivities(@PathVariable Long householdId) {
        return activityService.getByHousehold(householdId);
    }

   @GetMapping("/{householdId}/footprint")
public FootprintResponse getFootprint(
        @PathVariable Long householdId,
        @RequestParam String month) {

    try {
        return footprintService.getMonthlyFootprint(
                householdId, YearMonth.parse(month));
    } catch (java.time.format.DateTimeParseException ex) {
        throw new IllegalArgumentException(
                "Invalid month format. Use YYYY-MM, for example 2026-09");
    }
}
}