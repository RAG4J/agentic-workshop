package org.rag4j.agent.embabel;

import com.embabel.agent.api.common.autonomy.AgentInvocation;
import com.embabel.agent.api.common.autonomy.Autonomy;
import com.embabel.agent.api.common.autonomy.AutonomyProperties;
import com.embabel.agent.core.*;
import com.embabel.agent.spi.support.LlmRanker;
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

        // Find the agent through the platform and invoke it
        AgentInvocation<Conversation> invocation = AgentInvocation.builder(agentPlatform)
                .options(options -> options
                        .verbosity(verbosity -> verbosity
                                .showPrompts(true)
                                .showPlanning(true)
                                .debug(true)))
                .build(Conversation.class);

        Conversation conversation = invocation.invoke(userMessage);

        // Add the response to the conversation messages
        Conversation.Message embellishedResponse = conversation.messages().getFirst();
        messages.add(embellishedResponse);

        logger.debug("Generated embellished response: {}", embellishedResponse);

        return new Conversation(messages);
    }

}
