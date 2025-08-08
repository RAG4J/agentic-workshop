package org.rag4j.agent;

public interface Memory {
    void storeConversation(String userId, Conversation conversation);

    Conversation retrieveConversation(String userId);
}
