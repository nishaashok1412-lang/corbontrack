package com.carbontrack.exception;

public class ChallengeNotFoundException extends RuntimeException {
    public ChallengeNotFoundException(Long id) {
        super("Challenge not found: " + id);
    }
}
