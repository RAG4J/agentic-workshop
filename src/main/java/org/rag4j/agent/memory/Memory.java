package org.rag4j.agent.memory;

import org.rag4j.agent.Conversation;

public interface Memory {
    void storeConversation(String userId, Conversation conversation);

    Conversation retrieveConversation(String userId);
}
