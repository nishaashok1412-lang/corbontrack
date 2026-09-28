package com.carbontrack.repository;

import com.carbontrack.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    Optional<Participation> findByHouseholdIdAndChallengeId(Long householdId, Long challengeId);
    List<Participation> findByHouseholdIdOrderByJoinedAtDesc(Long householdId);
    List<Participation> findByChallengeIdOrderByJoinedAtAsc(Long challengeId);
}
