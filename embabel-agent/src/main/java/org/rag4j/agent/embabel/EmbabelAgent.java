package org.rag4j.agent.embabel;

import com.embabel.agent.core.*;
import org.rag4j.agent.core.Agent;
import org.rag4j.agent.core.Conversation;
import org.rag4j.agent.core.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * EmbabelAgent is an implementation of the Agent interface that acts as the wrapper for the Embabel agent platform.
 * We need this wrapper to be able to switch between the different agent platforms without changing the codebase.
 */
public record EmbabelAgent(AgentPlatform agentPlatform) implements Agent {
    private static final Logger logger = LoggerFactory.getLogger(EmbabelAgent.class);

    /**
     * Invokes the agent with a user ID and a user message, returning a conversation
     * with an embellished response.
     *
     * @param userId      the ID of the user
     * @param userMessage the message from the user
     * @return a Conversation object containing the user's message and the agent's embellished response
     */
    @Override
    public Conversation invoke(String userId, Conversation.Message userMessage) {
        logger.info("EmbabelAgent processing message from user: {}", userId);
        logger.debug("User message: {}", userMessage.content());

        // Create a list to hold the conversation messages
        List<Conversation.Message> messages = new ArrayList<>();
        messages.add(userMessage);

        com.embabel.agent.core.Agent embabelAgent =
                agentPlatform.agents().stream().filter(agent -> agent.getName().toLowerCase().contains("talks")).findFirst()
                        .orElseThrow(() -> new IllegalStateException("No agent found for generating embellished responses"));

        AgentProcess process = agentPlatform.createAgentProcessFrom(
                embabelAgent,
                ProcessOptions.builder()
                        .budget(Budget.builder().tokens(Budget.DEFAULT_TOKEN_LIMIT).build())
                        .verbosity(Verbosity.builder().showPrompts(true).showLlmResponses(true).showPlanning(true).build())
                        .build(),
                userMessage
        );

        CompletableFuture<AgentProcess> completableFuture = agentPlatform.start(process);
        Conversation.Message embellishedResponse;
        try {
            AgentProcess completedProcess = completableFuture.get(); // Waits for completion
            Object o = completedProcess.lastResult();// Replace with actual method
            if (o instanceof Conversation(List<Conversation.Message> messages1)) {
                embellishedResponse = messages1.getFirst();
            } else {
                logger.warn("Expected Conversation.Message but got: {}", o.getClass().getName());
                embellishedResponse = new Conversation.Message("Sorry, I couldn't generate a response.", Sender.ASSISTANT);
            }
        } catch (Exception e) {
            logger.error("Error processing agent response", e);
            embellishedResponse = new Conversation.Message("Sorry, I couldn't generate a response.", Sender.ASSISTANT);
        }

        messages.add(embellishedResponse);

        logger.debug("Generated embellished response: {}", embellishedResponse);

        return new Conversation(messages);
    }

}
