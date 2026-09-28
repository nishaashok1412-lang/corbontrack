package com.carbontrack.dto;

import com.carbontrack.entity.ActivityType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class CreateActivityRequest {
    @NotNull(message = "activityDate is required")
    private LocalDate activityDate;

    @NotNull(message = "activityType is required")
    private ActivityType activityType;

    @NotNull(message = "quantity is required")
    @Positive(message = "quantity must be greater than 0")
    private Double quantity;

    public LocalDate getActivityDate() { return activityDate; }
    public ActivityType getActivityType() { return activityType; }
    public Double getQuantity() { return quantity; }
    public void setActivityDate(LocalDate activityDate) { this.activityDate = activityDate; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
}
