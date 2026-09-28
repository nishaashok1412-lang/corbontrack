package com.carbontrack.service;

import com.carbontrack.dto.ParticipationResponse;
import com.carbontrack.entity.Challenge;
import com.carbontrack.entity.Household;
import com.carbontrack.entity.Participation;
import com.carbontrack.exception.BusinessRuleException;
import com.carbontrack.repository.ParticipationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final HouseholdService householdService;
    private final ChallengeService challengeService;
    private final CarbonFootprintService footprintService;

    public ParticipationService(
            ParticipationRepository participationRepository,
            HouseholdService householdService,
            ChallengeService challengeService,
            CarbonFootprintService footprintService) {
        this.participationRepository = participationRepository;
        this.householdService = householdService;
        this.challengeService = challengeService;
        this.footprintService = footprintService;
    }

    public ParticipationResponse join(Long householdId, Long challengeId) {
        Household household = householdService.findEntity(householdId);
        Challenge challenge = challengeService.findEntity(challengeId);

        if (participationRepository
                .findByHouseholdIdAndChallengeId(householdId, challengeId).isPresent()) {
            throw new BusinessRuleException("Household already participates in this challenge");
        }

        LocalDate today = LocalDate.now();
        if (today.isBefore(challenge.getStartDate()) || today.isAfter(challenge.getEndDate())) {
            throw new BusinessRuleException("Challenge is not active today");
        }

        Participation participation = new Participation();
        participation.setHousehold(household);
        participation.setChallenge(challenge);
        participation.setJoinedAt(LocalDateTime.now());

        return toResponse(participationRepository.save(participation));
    }
    @Transactional
    public List<ParticipationResponse> getByHousehold(Long householdId) {
        householdService.findEntity(householdId);
        return participationRepository.findByHouseholdIdOrderByJoinedAtDesc(householdId)
                .stream().map(this::toResponse).toList();
    }
    @Transactional
    public List<ParticipationResponse> getByChallenge(Long challengeId) {
        challengeService.findEntity(challengeId);
        return participationRepository.findByChallengeIdOrderByJoinedAtAsc(challengeId)
                .stream().map(this::toResponse).toList();
    }

    private ParticipationResponse toResponse(Participation participation) {
        Challenge challenge = participation.getChallenge();
        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);

        double previous = footprintService
                .getMonthlyFootprint(participation.getHousehold().getId(), previousMonth)
                .totalCarbonEmissionKg();
        double current = footprintService
                .getMonthlyFootprint(participation.getHousehold().getId(), currentMonth)
                .totalCarbonEmissionKg();
        double reduction = footprintService.calculateReduction(previous, current);

        return new ParticipationResponse(
                participation.getId(),
                participation.getHousehold().getId(),
                challenge.getId(),
                challenge.getName(),
                challenge.getStartDate(),
                challenge.getEndDate(),
                challenge.getTargetReductionPercentage(),
                participation.getJoinedAt(),
                previous,
                current,
                reduction,
                reduction >= challenge.getTargetReductionPercentage()
        );
    }
}
