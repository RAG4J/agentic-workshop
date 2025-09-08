package org.rag4j.agent;

import java.util.List;

import com.openai.client.OpenAIClient;
import org.rag4j.agent.core.Agent;
import org.rag4j.agent.core.ConferenceTalksRepository;
import org.rag4j.agent.memory.Memory;
import org.rag4j.agent.memory.WindowedConversationMemory;
import org.rag4j.agent.reasoning.OpenAIReasoning;
import org.rag4j.agent.reasoning.SystemPrompt;
import org.rag4j.agent.tools.AgentAsTool;
import org.rag4j.agent.tools.FindTalksBySpeaker;
import org.rag4j.agent.tools.FindTalksByTitle;
import org.rag4j.agent.tools.Tool;
import org.rag4j.agent.tools.ToolRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("plain-multi")
public class PlainMultiAgentConfig {

    @Bean(name = "agentRegistry")
    public ToolRegistry agentRegistry(@Qualifier("talksAgent") Agent agent, @Qualifier("scifiAgent") Agent scifiAgent) {
        List<Tool> tools = List.of(
                new AgentAsTool("talks_agent", "Agent to lookup talks based on the question from the user.", agent),
                new AgentAsTool("scifi_agent", "Agent that talks about science fiction.", scifiAgent)
        );
        return new ToolRegistry(tools);
    }

    @Bean(name = "talksAgent")
    public Agent talksAgent(OpenAIClient openAIClient,
                            Memory memory,
                            @Value("${agent.plain.reasoning.max-steps: 5}") int maxReasoningSteps,
                            @Qualifier("toolRegistry") ToolRegistry toolRegistry) {

        SystemPrompt systemPrompt = new SystemPrompt(
                "Conference Talks Agent",
                "You are an AI agent that answers questions about conference talks.",
                toolRegistry);
        OpenAIReasoning openAIReasoning = new OpenAIReasoning(openAIClient, systemPrompt);

        return new PlainJavaAgent(openAIReasoning, memory, maxReasoningSteps, toolRegistry);
    }

    @Bean(name = "orchestrator")
    public Agent orchestratorAgent(OpenAIClient openAIClient,
            Memory memory,
            @Value("${agent.plain.reasoning.max-steps: 5}") int maxReasoningSteps,
            @Qualifier("agentRegistry") ToolRegistry toolRegistry) {

        SystemPrompt systemPrompt = new SystemPrompt(
                "Conference Talks Agent",
                "You are an AI agent that answers questions.",
                toolRegistry);
        OpenAIReasoning openAIReasoning = new OpenAIReasoning(openAIClient, systemPrompt);

        return new PlainJavaAgent(openAIReasoning, memory, maxReasoningSteps, toolRegistry);
    }

    @Bean(name = "scifiAgent")
    public Agent scifiAgent(OpenAIClient openAIClient,
            Memory memory,
            @Value("${agent.plain.reasoning.max-steps: 5}") int maxReasoningSteps) {

        SystemPrompt systemPrompt = new SystemPrompt(
                "SciFi Conference Inspirator",
                "You are an AI agent that talks science fiction.");
        OpenAIReasoning openAIReasoning = new OpenAIReasoning(openAIClient, systemPrompt);

        return new PlainJavaAgent(openAIReasoning, memory, maxReasoningSteps);
    }
}
