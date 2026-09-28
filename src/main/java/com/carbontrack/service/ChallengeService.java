package com.carbontrack.service;

import com.carbontrack.dto.ChallengeResponse;
import com.carbontrack.dto.CreateChallengeRequest;
import com.carbontrack.entity.Challenge;
import com.carbontrack.exception.BusinessRuleException;
import com.carbontrack.exception.ChallengeNotFoundException;
import com.carbontrack.repository.ChallengeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository repository;

    public ChallengeService(ChallengeRepository repository) {
        this.repository = repository;
    }

    public ChallengeResponse create(CreateChallengeRequest request) {
        validateDates(request);

        Challenge challenge = new Challenge();
        challenge.setName(request.getName().trim());
        challenge.setDescription(request.getDescription());
        challenge.setTargetReductionPercentage(request.getTargetReductionPercentage());
        challenge.setStartDate(request.getStartDate());
        challenge.setEndDate(request.getEndDate());

        return toResponse(repository.save(challenge));
    }

    public List<ChallengeResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public ChallengeResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public ChallengeResponse update(Long id, CreateChallengeRequest request) {
        validateDates(request);
        Challenge challenge = findEntity(id);
        challenge.setName(request.getName().trim());
        challenge.setDescription(request.getDescription());
        challenge.setTargetReductionPercentage(request.getTargetReductionPercentage());
        challenge.setStartDate(request.getStartDate());
        challenge.setEndDate(request.getEndDate());
        return toResponse(repository.save(challenge));
    }

    public void delete(Long id) {
        repository.delete(findEntity(id));
    }

    public Challenge findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ChallengeNotFoundException(id));
    }

    private void validateDates(CreateChallengeRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessRuleException("Challenge start date cannot be after end date");
        }
    }

    private ChallengeResponse toResponse(Challenge challenge) {
        return new ChallengeResponse(
                challenge.getId(),
                challenge.getName(),
                challenge.getDescription(),
                challenge.getTargetReductionPercentage(),
                challenge.getStartDate(),
                challenge.getEndDate()
        );
    }
}
