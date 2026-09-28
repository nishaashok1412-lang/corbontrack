package com.carbontrack.controller;

import com.carbontrack.dto.ParticipationResponse;
import com.carbontrack.service.ParticipationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ParticipationController {

    private final ParticipationService service;

    public ParticipationController(ParticipationService service) {
        this.service = service;
    }

    @PostMapping("/challenges/{challengeId}/join/{householdId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationResponse join(
            @PathVariable Long challengeId,
            @PathVariable Long householdId) {
        return service.join(householdId, challengeId);
    }

    @GetMapping("/households/{householdId}/challenges")
    public List<ParticipationResponse> getByHousehold(@PathVariable Long householdId) {
        return service.getByHousehold(householdId);
    }

    @GetMapping("/challenges/{challengeId}/participants")
    public List<ParticipationResponse> getByChallenge(@PathVariable Long challengeId) {
        return service.getByChallenge(challengeId);
    }
}
