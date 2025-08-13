package org.rag4j.agent;

import org.rag4j.agent.core.Agent;
import org.rag4j.agent.memory.Memory;
import org.rag4j.agent.memory.WindowedConversationMemory;
import org.rag4j.agent.reasoning.OpenAIReasoning;
import org.rag4j.agent.reasoning.Reasoning;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("plain")
public class PlainAgentConfig {

    @Bean
    public Reasoning openAIReasoning(
            @Value("${openai.proxy.url}") String openAIProxyUrl,
            @Value("${openai.proxy.token}") String openAIProxyToken

    ) {
        return new OpenAIReasoning(openAIProxyUrl, openAIProxyToken);
    }

    @Bean
    public Memory memory(@Value("${agent.plain.conversation.max-size:10}") int maxConversationSize) {
        return new WindowedConversationMemory(maxConversationSize);
    }


    @Bean
    public Agent plainJavaAgent(Reasoning reasoning, Memory memory, @Value("${agent.plain.reasoning.max-steps: 5}") int maxReasoningSteps) {
        return new PlainJavaAgent(reasoning, memory, maxReasoningSteps);
    }
}
