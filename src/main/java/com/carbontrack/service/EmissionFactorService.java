package com.carbontrack.service;

import com.carbontrack.entity.ActivityType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmissionFactorService {

    /*
     * Fixed project emission factors.
     * Unit: kg CO2e per unit of activity.
     * These are deliberately fixed/documented assumptions for this assignment.
     */
    private static final Map<ActivityType, Double> FACTORS = Map.of(
            ActivityType.CAR_COMMUTE, 0.21,
            ActivityType.BUS_COMMUTE, 0.08,
            ActivityType.ELECTRICITY, 0.82,
            ActivityType.WASTE, 0.50
    );

    public double getFactor(ActivityType type) {
        Double factor = FACTORS.get(type);
        if (factor == null) {
            throw new IllegalArgumentException("No emission factor configured for " + type);
        }
        return factor;
    }
}
