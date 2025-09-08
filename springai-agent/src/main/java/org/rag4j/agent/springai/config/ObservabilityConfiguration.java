package org.rag4j.agent.springai.config;

import org.rag4j.agent.springai.advisor.ObservabilityAdvisor;
import org.rag4j.agent.springai.service.ChatLoggingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties(ObservabilityProperties.class)
@ConditionalOnProperty(name = "observability.enabled", havingValue = "true", matchIfMissing = true)
@Profile({"springai", "springai-multi"})
public class ObservabilityConfiguration {

    @Bean
    public ChatLoggingService chatLoggingService(ObservabilityProperties properties) {
        return new ChatLoggingService(properties);
    }

    @Bean
    public ObservabilityAdvisor observabilityAdvisor(ObservabilityProperties properties, 
                                                    ChatLoggingService loggingService) {
        return new ObservabilityAdvisor(properties, loggingService);
    }
}
