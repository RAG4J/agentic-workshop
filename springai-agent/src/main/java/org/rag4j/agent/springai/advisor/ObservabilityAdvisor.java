package org.rag4j.agent.springai.advisor;

import org.rag4j.agent.springai.config.ObservabilityProperties;
import org.rag4j.agent.springai.model.ChatInteractionLog;
import org.rag4j.agent.springai.service.ChatLoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ObservabilityAdvisor implements CallAdvisor {
    
    private static final Logger logger = LoggerFactory.getLogger(ObservabilityAdvisor.class);
    
    private final ObservabilityProperties properties;
    private final ChatLoggingService loggingService;
    private final Map<String, InteractionContext> activeInteractions = new ConcurrentHashMap<>();

    public ObservabilityAdvisor(ObservabilityProperties properties, ChatLoggingService loggingService) {
        this.properties = properties;
        this.loggingService = loggingService;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        if (!properties.isEnabled()) {
            return callAdvisorChain.nextCall(chatClientRequest);
        }

        String interactionId = UUID.randomUUID().toString();
        ChatInteractionLog interactionLog = new ChatInteractionLog();
        interactionLog.setInteractionId(interactionId);
        
        long startTime = System.currentTimeMillis();
        
        // Store interaction context
        InteractionContext context = new InteractionContext(interactionLog, startTime);
        activeInteractions.put(interactionId, context);

        try {
            // Log request
            if (properties.isLogRequests()) {
                logRequest(chatClientRequest, interactionLog);
            }

            // Execute the chain
            ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

            // Log response
            if (properties.isLogResponses()) {
                logResponse(chatClientResponse, interactionLog);
            }

            // Calculate duration and finalize log
            long endTime = System.currentTimeMillis();
            interactionLog.setDurationMs(endTime - startTime);
            
            // Add metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("advisor_name", getName());
            metadata.put("advisor_order", getOrder());
            interactionLog.setMetadata(metadata);

            // Write the log
            loggingService.logInteraction(interactionLog);

            return chatClientResponse;
            
        } catch (Exception e) {
            // Log error
            if (properties.isLogErrors()) {
                logError(e, interactionLog);
            }
            
            // Calculate duration and finalize log even on error
            long endTime = System.currentTimeMillis();
            interactionLog.setDurationMs(endTime - startTime);
            
            // Write the error log
            loggingService.logInteraction(interactionLog);
            
            // Re-throw the exception
            throw e;
        } finally {
            // Clean up
            activeInteractions.remove(interactionId);
        }
    }

    @Override
    public String getName() {
        return "ObservabilityAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private void logRequest(ChatClientRequest request, ChatInteractionLog interactionLog) {
        try {
            interactionLog.setRequest(request);
            
        } catch (Exception e) {
            logger.warn("Failed to log request details", e);
        }
    }

    private void logResponse(ChatClientResponse response, ChatInteractionLog interactionLog) {
        try {
            interactionLog.setResponse(response);
            
        } catch (Exception e) {
            logger.warn("Failed to log response details", e);
        }
    }

    private void logError(Exception error, ChatInteractionLog interactionLog) {
        try {
            interactionLog.setError(error.getMessage());
            
        } catch (Exception e) {
            logger.warn("Failed to log error details", e);
        }
    }

    private static class InteractionContext {
        private final ChatInteractionLog interactionLog;
        private final long startTime;

        public InteractionContext(ChatInteractionLog interactionLog, long startTime) {
            this.interactionLog = interactionLog;
            this.startTime = startTime;
        }

        public ChatInteractionLog getInteractionLog() {
            return interactionLog;
        }

        public long getStartTime() {
            return startTime;
        }
    }
}
