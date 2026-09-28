package com.carbontrack.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @Column(nullable = false)
    private LocalDate activityDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActivityType activityType;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Double carbonEmission;

    public Long getId() { return id; }
    public Household getHousehold() { return household; }
    public LocalDate getActivityDate() { return activityDate; }
    public ActivityType getActivityType() { return activityType; }
    public Double getQuantity() { return quantity; }
    public Double getCarbonEmission() { return carbonEmission; }

    public void setHousehold(Household household) { this.household = household; }
    public void setActivityDate(LocalDate activityDate) { this.activityDate = activityDate; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public void setCarbonEmission(Double carbonEmission) { this.carbonEmission = carbonEmission; }
}
