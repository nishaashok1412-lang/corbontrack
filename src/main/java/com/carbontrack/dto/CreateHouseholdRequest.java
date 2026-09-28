package com.carbontrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateHouseholdRequest {
    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "membersCount is required")
    @Positive(message = "membersCount must be greater than 0")
    private Integer membersCount;

    public String getName() { return name; }
    public Integer getMembersCount() { return membersCount; }
    public void setName(String name) { this.name = name; }
    public void setMembersCount(Integer membersCount) { this.membersCount = membersCount; }
}
