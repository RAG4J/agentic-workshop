package org.rag4j.agent;

import org.rag4j.agent.core.Agent;
import org.rag4j.agent.core.ConferenceTalksRepository;
import org.rag4j.agent.memory.Memory;
import org.rag4j.agent.memory.WindowedConversationMemory;
import org.rag4j.agent.reasoning.OpenAIReasoning;
import org.rag4j.agent.reasoning.Reasoning;
import org.rag4j.agent.reasoning.SystemPrompt;
import org.rag4j.agent.tools.AgenticTool;
import org.rag4j.agent.tools.FindTalksBySpeaker;
import org.rag4j.agent.tools.FindTalksByTitle;
import org.rag4j.agent.tools.ToolRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("plain")
public class PlainAgentConfig {

    @Bean
    public Memory memory(@Value("${agent.plain.conversation.max-size:10}") int maxConversationSize) {
        return new WindowedConversationMemory(maxConversationSize);
    }

    @Bean
    public ConferenceTalksRepository conferenceTalksRepository() {
        return new ConferenceTalksRepository();
    }

    @Bean
    public ToolRegistry toolRegistry(ConferenceTalksRepository conferenceTalksRepository) {
        List<AgenticTool> tools = List.of(
                new FindTalksByTitle(conferenceTalksRepository),
                new FindTalksBySpeaker(conferenceTalksRepository)
        );
        return new ToolRegistry(tools);
    }

    @Bean
    public Agent plainJavaAgent(
            @Value("${openai.proxy.url}") String openAIProxyUrl,
            @Value("${openai.proxy.token}") String openAIProxyToken,
            Memory memory,
            @Value("${agent.plain.reasoning.max-steps: 5}") int maxReasoningSteps,
            ToolRegistry toolRegistry) {

        SystemPrompt systemPrompt = new SystemPrompt(
                "Conference Talks Agent",
                "You are an AI agent that answers questions about conference talks.",
                toolRegistry);
        OpenAIReasoning openAIReasoning = new OpenAIReasoning(openAIProxyUrl, openAIProxyToken, systemPrompt);

        return new PlainJavaAgent(openAIReasoning, memory, maxReasoningSteps, toolRegistry);
    }
}
