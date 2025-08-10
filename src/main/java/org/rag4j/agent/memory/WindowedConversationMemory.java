package org.rag4j.agent.memory;

import org.jetbrains.annotations.NotNull;
import org.rag4j.agent.Conversation;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("windowedConversationMemory")
public class WindowedConversationMemory implements Memory {
    private final Map<String, Conversation> conversationStore = new HashMap<>();

    @Override
    public void storeConversation(String userId, @NotNull Conversation conversation) {
        // If the conversation exceeds a certain size, trim it from the start
        if (conversation.messages().size() > 10) { // Example size limit
            conversation.messages().subList(0, conversation.messages().size() - 10).clear();
        }
        conversationStore.put(userId, conversation);
    }

    @Override
    public Conversation retrieveConversation(String userId) {
        // Check if the conversation exists, return an empty conversation if not
        if (!conversationStore.containsKey(userId)) {
            return new Conversation(new java.util.ArrayList<>());
        }
        return conversationStore.get(userId);
    }
}
