package org.rag4j.agent.springai.bedrock;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ProxyBedrockProperties.class)
@Profile("bedrock-proxy")
public class BedrockConfig {
    @Bean
    public RestClient proxyRestClient(ProxyBedrockProperties props) {
        return RestClient.builder()
                .baseUrl(props.baseUrl())
                .defaultHeader("Authorization", "Bearer " + props.apiKey())
                .build();
    }

    @Bean
    public ChatModel chatModel(ToolCallingChatOptions options, ObservationRegistry observationRegistry, ToolCallingManager toolCallingManager, RestClient proxyRestClient, ProxyBedrockProperties props) {

        return new BedrockProxyChatModel(proxyRestClient, props.modelId());
    }

    @Bean
    ToolCallingChatOptions from(ProxyBedrockProperties props) {
        return ToolCallingChatOptions.builder()
                .model(props.modelId())
                .build();
    }

    @Bean
    ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }


}
