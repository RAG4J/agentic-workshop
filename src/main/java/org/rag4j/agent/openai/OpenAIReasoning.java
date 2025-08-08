package org.rag4j.agent.openai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;
import com.openai.models.responses.*;
import org.rag4j.agent.Conversation;
import org.rag4j.agent.Reasoning;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAIReasoning implements Reasoning {

    @Value("${openai.api.key}")
    private String openaiApiKey;


    public Conversation.Message reason(Conversation.Message userMessage, Conversation conversation) {
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(openaiApiKey) // Ensure you set your OpenAI API key in the environment
                .build();

        ChatCompletionCreateParams.Builder createParamsBuilder = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_1_MINI);

        for (Conversation.Message message : conversation.messages()) {
            if (message.sender().equals("User")) {
                createParamsBuilder.addUserMessage(message.content());
            } else if (message.sender().equals("Assistant")) {
                createParamsBuilder.addAssistantMessage(message.content());
            }
        }

        createParamsBuilder
                .addUserMessage(userMessage.content());


        List<ChatCompletionMessage> messages =
                client.chat().completions().create(createParamsBuilder.build()).choices().stream()
                        .map(ChatCompletion.Choice::message)
                        .toList();

        List<String> output = messages.stream()
                .flatMap(message -> message.content().stream())
                .toList();

        return new Conversation.Message(output.getFirst(), "Assistant"); // Return the first output text as a simple response
    }
}
