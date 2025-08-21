package org.rag4j.agent.springai.multi;

import org.rag4j.agent.core.Agent;
import org.rag4j.agent.springai.ConferenceTalksTools;
import org.rag4j.agent.springai.TalksAgent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("springai-multi")
public class SpringAIMultiAgentConfig {

    @Bean(name = "reasoningChatClient")
    public ChatClient reasoningChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().build();
    }

    @Bean(name = "conversationChatClient")
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Bean
    public AgentRegistry agentRegister(TalksAgent talksAgent, SciFiAgent sciFiAgent) {
        AgentRegistry agentRegistry = new AgentRegistry();
        agentRegistry.registerAgent("Conference Talks Specialist",  talksAgent);
        agentRegistry.registerAgent("Science Fiction Geek",  sciFiAgent);
        return agentRegistry;
    }

    @Bean(name = "orchestrator")
    public Agent routerAgent(@Qualifier("reasoningChatClient") ChatClient chatClient, AgentRegistry agentRegistry) {
        return new RouterAgent(chatClient, agentRegistry);
    }

    @Bean
    public TalksAgent springAIAgent(@Qualifier("conversationChatClient") ChatClient chatClient,
                                    ChatMemory chatMemory,
                                    ConferenceTalksTools conferenceTalksTools) {
        return new TalksAgent(chatClient, chatMemory, conferenceTalksTools);
    }

    @Bean
    public SciFiAgent sciFiAgent(@Qualifier("conversationChatClient") ChatClient chatClient, ChatMemory chatMemory) {
        return new SciFiAgent(chatClient, chatMemory);
    }
}
