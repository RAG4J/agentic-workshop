package org.rag4j.agent.springai;

import io.modelcontextprotocol.client.McpSyncClient;
import org.rag4j.agent.core.Agent;
import org.rag4j.agent.springai.advisor.ObservabilityAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("springai")
public class SpringAIAgentConfig {

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, List<McpSyncClient> mcpSyncClients) {
        return builder
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients))
                .build();
    }

    @Bean(name = "orchestrator")
    public Agent talksSpringAIAgent(ChatClient chatClient, ChatMemory chatMemory,
                                    ConferenceTalksTools conferenceTalksTools,
                                    ObservabilityAdvisor observabilityAdvisor) {
        return new TalksAgent(chatClient, chatMemory, conferenceTalksTools, observabilityAdvisor);
    }
}
