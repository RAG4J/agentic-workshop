package org.rag4j.evals.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the evaluation score types
 */
public enum ScoreType {
    GOOD("Good"),
    BAD("Bad"),
    UNKNOWN("Unknown");
    
    private final String displayValue;
    
    ScoreType(String displayValue) {
        this.displayValue = displayValue;
    }
    
    @JsonValue
    public String getDisplayValue() {
        return displayValue;
    }
    
    public String getCssClass() {
        return switch (this) {
            case GOOD -> "text-success";
            case BAD -> "text-danger";
            case UNKNOWN -> "text-secondary";
        };
    }
    
    public String getBadgeClass() {
        return switch (this) {
            case GOOD -> "badge bg-success";
            case BAD -> "badge bg-danger";
            case UNKNOWN -> "badge bg-secondary";
        };
    }
    
    public static ScoreType fromString(String value) {
        if (value == null) return UNKNOWN;
        return switch (value.toLowerCase()) {
            case "good" -> GOOD;
            case "bad" -> BAD;
            default -> UNKNOWN;
        };
    }
    
    @Override
    public String toString() {
        return displayValue;
    }
}
