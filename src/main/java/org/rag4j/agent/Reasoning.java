package org.rag4j.agent;

public interface Reasoning {
    Conversation.Message reason(Conversation.Message userMessage, Conversation conversation);
}
