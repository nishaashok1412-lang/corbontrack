package com.carbontrack.service;

import com.carbontrack.dto.ActivityResponse;
import com.carbontrack.dto.CreateActivityRequest;
import com.carbontrack.entity.ActivityLog;
import com.carbontrack.entity.Household;
import com.carbontrack.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityLogRepository activityRepository;
    private final HouseholdService householdService;
    private final EmissionFactorService emissionFactorService;

    public ActivityService(
            ActivityLogRepository activityRepository,
            HouseholdService householdService,
            EmissionFactorService emissionFactorService) {
        this.activityRepository = activityRepository;
        this.householdService = householdService;
        this.emissionFactorService = emissionFactorService;
    }

    public ActivityResponse create(Long householdId, CreateActivityRequest request) {
        Household household = householdService.findEntity(householdId);

        double factor = emissionFactorService.getFactor(request.getActivityType());
        double carbon = round(request.getQuantity() * factor);

        ActivityLog activity = new ActivityLog();
        activity.setHousehold(household);
        activity.setActivityDate(request.getActivityDate());
        activity.setActivityType(request.getActivityType());
        activity.setQuantity(request.getQuantity());
        activity.setCarbonEmission(carbon);

        return toResponse(activityRepository.save(activity));
    }

    public List<ActivityResponse> getByHousehold(Long householdId) {
        householdService.findEntity(householdId);
        return activityRepository.findByHouseholdIdOrderByActivityDateAsc(householdId)
                .stream().map(this::toResponse).toList();
    }

    private ActivityResponse toResponse(ActivityLog activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getHousehold().getId(),
                activity.getActivityDate(),
                activity.getActivityType(),
                activity.getQuantity(),
                activity.getCarbonEmission()
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
