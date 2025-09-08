package org.rag4j.agent.springai.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatInteractionLog {

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("interaction_id")
    private String interactionId;

    @JsonProperty("request")
    private ChatClientRequest request;

    @JsonProperty("response")
    private ChatClientResponse response;

    @JsonProperty("error")
    private String error;

    @JsonProperty("duration_ms")
    private Long durationMs;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    public ChatInteractionLog() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(String interactionId) {
        this.interactionId = interactionId;
    }

    public ChatClientRequest getRequest() {
        return request;
    }

    public void setRequest(ChatClientRequest request) {
        this.request = request;
    }

    public ChatClientResponse getResponse() {
        return response;
    }

    public void setResponse(ChatClientResponse response) {
        this.response = response;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }




}
