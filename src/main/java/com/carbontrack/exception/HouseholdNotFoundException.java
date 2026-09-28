package com.carbontrack.exception;

public class HouseholdNotFoundException extends RuntimeException {
    public HouseholdNotFoundException(Long id) {
        super("Household not found: " + id);
    }
}
