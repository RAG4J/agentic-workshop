package org.rag4j.agent.springai;

import org.rag4j.agent.core.Agent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("springai")
public class SpringAIAgentConfig {

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

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Bean(name = "orchestrator")
    public Agent springAIAgent(ChatClient chatClient) {
        return new SpringAIAgent(chatClient);
    }
}
