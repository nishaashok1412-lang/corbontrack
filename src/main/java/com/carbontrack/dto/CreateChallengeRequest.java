package com.carbontrack.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateChallengeRequest {
    @NotBlank(message = "name is required")
    private String name;

    private String description;

    @NotNull(message = "targetReductionPercentage is required")
    @DecimalMin(value = "0.01", message = "targetReductionPercentage must be greater than 0")
    @DecimalMax(value = "100.0", message = "targetReductionPercentage cannot exceed 100")
    private Double targetReductionPercentage;

    @NotNull(message = "startDate is required")
    private LocalDate startDate;

    @NotNull(message = "endDate is required")
    private LocalDate endDate;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getTargetReductionPercentage() { return targetReductionPercentage; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setTargetReductionPercentage(Double targetReductionPercentage) {
        this.targetReductionPercentage = targetReductionPercentage;
    }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
