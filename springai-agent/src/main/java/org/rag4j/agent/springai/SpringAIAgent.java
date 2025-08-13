package org.rag4j.agent.springai;

import org.rag4j.agent.core.Agent;
import org.rag4j.agent.core.Conversation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

import java.util.List;

import static org.rag4j.agent.core.Sender.ASSISTANT;

/**
 * Spring AI-based implementation of the Agent interface.
 * Uses Spring AI's ChatClient for LLM interactions.
 */
public class SpringAIAgent implements Agent {
    private static final Logger logger = LoggerFactory.getLogger(SpringAIAgent.class);
    private final ChatClient chatClient;

    public SpringAIAgent(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Conversation invoke(String userId, Conversation.Message userMessage) {
        logger.info("SpringAIAgent invoke userId = {}, userMessage = {}", userId, userMessage);
        String content = this.chatClient.prompt()
                .tools(new ConferenceTalksTools())
                .system("You are an AI agent that answers questions about conference talks.")
                .user(userMessage.content())
                .advisors(a -> a.param(ChatMemory.DEFAULT_CONVERSATION_ID, userId))
                .call()
                .content();
        logger.info("SpringAIAgent invoke content = {}", content);
        return new Conversation(List.of(userMessage, new Conversation.Message(content, ASSISTANT)));
    }

}
