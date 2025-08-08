package org.rag4j.agent;

import org.springframework.stereotype.Service;

@Service
public class Agent {
    private final Reasoning reasoning;
    private final Memory memory;

    public Agent(Reasoning reasoning, Memory memory) {
        this.reasoning = reasoning;
        this.memory = memory;
    }


    public Conversation invoke(String userId, Conversation.Message message) {
        Conversation conversation = memory.retrieveConversation(userId);
        // Use the reasoning service to generate a response
        Conversation.Message response = reasoning.reason(message, conversation);

        // Store the conversation in memory
        conversation.messages().add(message);
        conversation.messages().add(response);

        memory.storeConversation(userId, conversation);

        // Create a new conversation with the response
        return conversation;
    }
}
