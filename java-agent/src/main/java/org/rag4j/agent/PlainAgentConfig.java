package org.rag4j.agent;

import java.util.List;

import com.openai.client.OpenAIClient;
import org.rag4j.agent.core.Agent;
import org.rag4j.agent.core.ConferenceTalksRepository;
import org.rag4j.agent.memory.Memory;
import org.rag4j.agent.memory.WindowedConversationMemory;
import org.rag4j.agent.reasoning.OpenAIReasoning;
import org.rag4j.agent.reasoning.SystemPrompt;
import org.rag4j.agent.tools.FindTalksBySpeaker;
import org.rag4j.agent.tools.FindTalksByTitle;
import org.rag4j.agent.tools.Tool;
import org.rag4j.agent.tools.ToolRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("plain")
public class PlainAgentConfig {

    @Bean(name = "orchestrator")
    public Agent orchestratorAgent(OpenAIClient openAIClient,
                                   Memory memory,
                                   PlainAgentReasoningConfigProperties agentConfigProperties,
                                   ToolRegistry toolRegistry) {

        SystemPrompt systemPrompt = new SystemPrompt(
                "Conference Talks Agent",
                "You are an AI agent that answers questions about conference talks.",
                toolRegistry);
        OpenAIReasoning openAIReasoning = new OpenAIReasoning(openAIClient, systemPrompt);

        return new PlainJavaAgent(openAIReasoning, memory, agentConfigProperties.getMaxReasoningSteps(), toolRegistry);
    }
}
