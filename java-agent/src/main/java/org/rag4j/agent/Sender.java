package org.rag4j.agent;

public enum Sender {
    SYSTEM("System"),
    USER("User"),
    ASSISTANT("Assistant"),
    OBSERVATION("Observation");

    private final String displayName;

    Sender(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public String toString() {
        return displayName;
    }
}
