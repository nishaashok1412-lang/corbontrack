package com.carbontrack.controller;

import com.carbontrack.dto.CreateHouseholdRequest;
import com.carbontrack.dto.HouseholdResponse;
import com.carbontrack.service.HouseholdService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/households")
public class HouseholdController {

    private final HouseholdService service;

    public HouseholdController(HouseholdService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HouseholdResponse create(@Valid @RequestBody CreateHouseholdRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<HouseholdResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public HouseholdResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public HouseholdResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CreateHouseholdRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
