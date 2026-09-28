package com.carbontrack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "participations",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_household_challenge",
        columnNames = {"household_id", "challenge_id"}
    )
)
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    void prePersist() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public Household getHousehold() { return household; }
    public Challenge getChallenge() { return challenge; }
    public LocalDateTime getJoinedAt() { return joinedAt; }

    public void setHousehold(Household household) { this.household = household; }
    public void setChallenge(Challenge challenge) { this.challenge = challenge; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
