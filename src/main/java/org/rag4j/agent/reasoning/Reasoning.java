package org.rag4j.agent.reasoning;

import org.rag4j.agent.Conversation;

public interface Reasoning {
    Conversation.Message reason(Conversation.Message userMessage, Conversation conversation);
}
