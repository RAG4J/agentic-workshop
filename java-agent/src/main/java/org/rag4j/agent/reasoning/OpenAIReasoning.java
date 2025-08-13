package org.rag4j.agent.reasoning;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;
import org.rag4j.agent.core.Conversation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.rag4j.agent.core.Sender.*;

/**
 * OpenAIReasoning is a service that interacts with the OpenAI API to perform reasoning tasks.
 * It uses the OpenAIClient to send messages and receive responses from the OpenAI model.
 */
public class OpenAIReasoning implements Reasoning {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIReasoning.class);

    private final OpenAIClient openAIClient;
    private final ChatModel chatModel;

    public OpenAIReasoning(String openAIProxyUrl, String openAIProxyToken) {
        this.openAIClient = OpenAIOkHttpClient.builder()
                .apiKey(openAIProxyToken)
                .baseUrl(openAIProxyUrl + "/openai")
                .build();
        this.chatModel = ChatModel.GPT_4_1_MINI;
    }

    public Conversation.Message reason(Conversation.Message userMessage, Conversation conversation) {
        String outputMessage = callLlm(userMessage, conversation);
        logger.debug("Received output message: {}", outputMessage);

        return new Conversation.Message(outputMessage, ASSISTANT.displayName());
    }

    private String callLlm(Conversation.Message userMessage, Conversation conversation) {
        ChatCompletionCreateParams.Builder createParamsBuilder = ChatCompletionCreateParams.builder()
                .model(this.chatModel)
                .addDeveloperMessage(SystemPrompt.build());

        prepareMessages(userMessage, conversation, createParamsBuilder);

        List<ChatCompletionMessage> messages =
                this.openAIClient.chat().completions().create(createParamsBuilder.build()).choices().stream()
                        .map(ChatCompletion.Choice::message)
                        .toList();

        List<String> output = messages.stream()
                .flatMap(message -> message.content().stream())
                .toList();
        // Check if the message is an action
        if (output.isEmpty()) {
            throw new IllegalStateException("No output received from OpenAI API.");
        }
        if (output.size() > 1) {
            logger.warn("Multiple messages received from OpenAI API, using the first one.");
        }

        String output_message = output.getFirst();
        logger.info("Output message: {}", output_message);
        return output_message;
    }

    private static void prepareMessages(Conversation.Message userMessage,
                                        Conversation conversation,
                                        ChatCompletionCreateParams.Builder createParamsBuilder) {
        for (Conversation.Message message : conversation.messages()) {
            if (message.sender().equals(USER.displayName())) {
                createParamsBuilder.addUserMessage(message.content());
            } else if (message.sender().equals(ASSISTANT.displayName())) {
                createParamsBuilder.addAssistantMessage(message.content());
            }
        }

        createParamsBuilder.addUserMessage(userMessage.content());
    }
}
