package org.rag4j.agent.reasoning;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;
import com.openai.services.blocking.ChatService;
import com.openai.services.blocking.chat.ChatCompletionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.rag4j.agent.Conversation;
import org.rag4j.agent.Sender;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OpenAIReasoningTest {
    private OpenAIReasoning openAIReasoning;
    private OpenAIClient mockClient;

    @BeforeEach
    void setUp() {
        mockClient = mock(OpenAIClient.class);
        // Use Mockito to mock static builder and OpenAIOkHttpClient
        try (MockedStatic<OpenAIOkHttpClient> mockedStatic = Mockito.mockStatic(OpenAIOkHttpClient.class)) {
            OpenAIOkHttpClient.Builder builder = mock(OpenAIOkHttpClient.Builder.class);
            when(builder.apiKey(any())).thenReturn(builder);
            when(builder.baseUrl(anyString())).thenReturn(builder);
            when(builder.build()).thenReturn(mockClient);
            mockedStatic.when(OpenAIOkHttpClient::builder).thenReturn(builder);
            openAIReasoning = new OpenAIReasoning("http://localhost", "token");
        }
    }

    @Test
    @DisplayName("Returns assistant message with LLM output on valid input")
    void returnsAssistantMessageWithLlmOutputOnValidInput() {
        Conversation.Message userMessage = new Conversation.Message("Hello", Sender.USER.displayName());
        Conversation conversation = new Conversation(List.of(userMessage));

        ChatCompletion mockCompletion = mock(ChatCompletion.class);
        ChatCompletion.Choice mockChoice = mock(ChatCompletion.Choice.class);
        ChatCompletionMessage mockCompletionMessage = mock(ChatCompletionMessage.class);
        ChatService mockChatService = mock(ChatService.class);
        ChatCompletionService mockChatCompletionService = mock(ChatCompletionService.class);

        when(mockClient.chat()).thenReturn(mockChatService);
        when(mockChatService.completions()).thenReturn(mockChatCompletionService);
        when(mockChatCompletionService.create(any(ChatCompletionCreateParams.class))).thenReturn(mockCompletion);
        when(mockCompletion.choices()).thenReturn(List.of(mockChoice));
        when(mockChoice.message()).thenReturn(mockCompletionMessage);
        when(mockCompletionMessage.content()).thenReturn(Optional.of("Hi there!"));

        Conversation.Message result = openAIReasoning.reason(userMessage, conversation);
        assertEquals("Hi there!", result.content());
        assertEquals(Sender.ASSISTANT.displayName(), result.sender());
    }


    @Test
    @DisplayName("Throws exception if OpenAIClient throws")
    void throwsExceptionIfOpenAIClientThrows() {
        Conversation.Message userMessage = new Conversation.Message("Hello", Sender.USER.displayName());
        Conversation conversation = new Conversation(List.of(userMessage));
        when(mockClient.chat()).thenThrow(new RuntimeException("API error"));

        assertThrows(RuntimeException.class, () -> openAIReasoning.reason(userMessage, conversation));
    }
}

