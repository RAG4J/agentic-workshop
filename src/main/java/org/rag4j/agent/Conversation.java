package org.rag4j.agent;

import java.util.List;

public record Conversation(List<Message> messages) {

    public record Message(String content, String sender) {
    }
}
