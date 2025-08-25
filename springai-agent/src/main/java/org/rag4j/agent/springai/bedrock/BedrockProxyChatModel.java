package org.rag4j.agent.springai.bedrock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.ai.chat.messages.MessageType.SYSTEM;

public record BedrockProxyChatModel(RestClient client, String modelId) implements ChatModel {
    private static final Logger logger = LoggerFactory.getLogger(BedrockProxyChatModel.class);

    @Override
    public ChatResponse call(Prompt prompt) {
        logger.info("BedrockProxyChatModel call modelId = {}, prompt = {}", modelId, prompt);

        // 1) Map Spring AI messages -> your proxy payload
        var messages = prompt.getInstructions().stream()
                .filter(message -> message.getMessageType() != SYSTEM)
                .map(m -> {
            var role = switch (m.getMessageType()) {
                case USER -> "user";
                case SYSTEM -> "system";
                case ASSISTANT -> "assistant";
                default -> "unknown";
            };
            return Map.<String, Object>of(
                    "role", role,
                    "content", List.of(Map.of("text", m.getText()))
            );
        }).toList();

        var systemMessages = prompt.getInstructions().stream()
                .filter(message -> message.getMessageType() == SYSTEM)
                .map(m -> List.of(Map.of("text", m.getText())
        )).toList();

        var payload = Map.of(
                "modelId", modelId,
                "system", systemMessages,
                "body", Map.of("messages", messages)
        );

        // 2) POST to your proxy
        var proxyResponse = client.post()
                .uri("/invoke-model")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(Map.class);

        // 3) Extract text back into ChatResponse
        // Adjust this to your proxy’s exact response shape.
        var outputText = Optional.ofNullable(proxyResponse)
                .map(r -> r.get("output"))
                .map(Object::toString)
                .orElseGet(() -> proxyResponse.toString());

        Generation generation = new Generation(new AssistantMessage(outputText));

        return ChatResponse.builder()
                .generations(List.of(generation))
                .build();
    }
}
