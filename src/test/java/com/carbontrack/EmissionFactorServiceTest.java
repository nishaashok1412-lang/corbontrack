package com.carbontrack;

import com.carbontrack.entity.ActivityType;
import com.carbontrack.service.EmissionFactorService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmissionFactorServiceTest {

    @Test
    void fixedFactorsAreReturned() {
        EmissionFactorService service = new EmissionFactorService();

        assertEquals(0.21, service.getFactor(ActivityType.CAR_COMMUTE));
        assertEquals(0.08, service.getFactor(ActivityType.BUS_COMMUTE));
        assertEquals(0.82, service.getFactor(ActivityType.ELECTRICITY));
        assertEquals(0.50, service.getFactor(ActivityType.WASTE));
    }
}
