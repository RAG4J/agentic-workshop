package org.rag4j.agent.springai;

import io.modelcontextprotocol.client.McpSyncClient;
import org.rag4j.agent.core.Agent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
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
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory, List<McpSyncClient> mcpSyncClients) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients))
                .build();
    }

    @Bean(name = "orchestrator")
    public Agent talksSpringAIAgent(ChatClient chatClient,  ChatMemory chatMemory, ConferenceTalksTools conferenceTalksTools) {
        return new TalksAgent(chatClient, chatMemory, conferenceTalksTools);
    }
}
