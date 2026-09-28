package com.carbontrack.controller;

import com.carbontrack.dto.LeaderboardResponse;
import com.carbontrack.service.LeaderboardService;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService service;

    public LeaderboardController(LeaderboardService service) {
        this.service = service;
    }

    @GetMapping
    public List<LeaderboardResponse> getLeaderboard(@RequestParam String month) {
        return service.getLeaderboard(YearMonth.parse(month));
    }
}
