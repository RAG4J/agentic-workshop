package org.rag4j.agent.springai.mutli;

import org.rag4j.agent.core.Agent;
import org.rag4j.agent.springai.SpringAIAgent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("springai-multi")
public class SpringAIMultiAgentConfig {

    @Bean
    public ChatModel chatModel(
            @Value("${openai.proxy.url}") String openAIProxyUrl,
            @Value("${openai.proxy.token}") String openAIProxyToken) {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(openAIProxyUrl + "/openai")
                .apiKey(openAIProxyToken)
                .build();

        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_4_1_MINI)
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(chatOptions)
                .build();

    }

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
    public AgentRegistry agentRegister(@Qualifier("talksAgent") Agent talksAgent, @Qualifier("sciFiAgent") Agent sciFiAgent) {
        AgentRegistry agentRegistry = new AgentRegistry();
        agentRegistry.registerAgent("Conference Talks Specialist",  talksAgent);
        agentRegistry.registerAgent("Science Fiction Geek",  sciFiAgent);
        return agentRegistry;
    }

    @Bean(name = "orchestrator")
    public Agent routerAgent(@Qualifier("reasoningChatClient") ChatClient chatClient, AgentRegistry agentRegistry) {
        return new RouterAgent(chatClient, agentRegistry);
    }

    @Bean(name = "talksAgent")
    public Agent springAIAgent(@Qualifier("conversationChatClient") ChatClient chatClient, ChatMemory chatMemory) {
        return new SpringAIAgent(chatClient, chatMemory);
    }

    @Bean(name = "sciFiAgent")
    public Agent sciFiAgent(@Qualifier("conversationChatClient") ChatClient chatClient, ChatMemory chatMemory) {
        return new SciFiAgent(chatClient, chatMemory);
    }
}
