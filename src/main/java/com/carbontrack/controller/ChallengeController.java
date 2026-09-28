package com.carbontrack.controller;

import com.carbontrack.dto.ChallengeResponse;
import com.carbontrack.dto.CreateChallengeRequest;
import com.carbontrack.service.ChallengeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService service;

    public ChallengeController(ChallengeService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChallengeResponse create(@Valid @RequestBody CreateChallengeRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ChallengeResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ChallengeResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ChallengeResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CreateChallengeRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
