package com.carbontrack.service;

import com.carbontrack.dto.CreateHouseholdRequest;
import com.carbontrack.dto.HouseholdResponse;
import com.carbontrack.entity.Household;
import com.carbontrack.exception.HouseholdNotFoundException;
import com.carbontrack.repository.HouseholdRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseholdService {

    private final HouseholdRepository repository;

    public HouseholdService(HouseholdRepository repository) {
        this.repository = repository;
    }

    public HouseholdResponse create(CreateHouseholdRequest request) {
        Household household = new Household();
        household.setName(request.getName().trim());
        household.setMembersCount(request.getMembersCount());
        return toResponse(repository.save(household));
    }

    public List<HouseholdResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public HouseholdResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public HouseholdResponse update(Long id, CreateHouseholdRequest request) {
        Household household = findEntity(id);
        household.setName(request.getName().trim());
        household.setMembersCount(request.getMembersCount());
        return toResponse(repository.save(household));
    }

    public void delete(Long id) {
        repository.delete(findEntity(id));
    }

    public Household findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new HouseholdNotFoundException(id));
    }

    private HouseholdResponse toResponse(Household household) {
        return new HouseholdResponse(
                household.getId(),
                household.getName(),
                household.getMembersCount(),
                household.getCreatedAt()
        );
    }
}
