package org.rag4j.agent.springai;

import org.rag4j.agent.core.ConferenceTalksRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"springai", "springai-multi"})
public class SpringAIConfigCommon {
    @Bean
    @Profile("!bedrock-proxy")
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
    public ConferenceTalksRepository conferenceTalksRepository() {
        return new ConferenceTalksRepository();
    }

    @Bean
    public ConferenceTalksTools conferenceTalksTools(ConferenceTalksRepository conferenceTalksRepository) {
        return new ConferenceTalksTools(conferenceTalksRepository);
    }
}
