package org.rag4j.agent.springai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.rag4j.agent.core.Conversation;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.rag4j.agent.core.Sender.ASSISTANT;
import static org.rag4j.agent.core.Sender.USER;

class ActionAgentTest {
    private ChatClient chatClient;
    private ChatMemory chatMemory;
    private ActionAgent actionAgent;

    @BeforeEach
    void setUp() {
        chatClient = mock(ChatClient.class);
        chatMemory = mock(ChatMemory.class);
        actionAgent = new ActionAgent(chatClient, chatMemory) {
            @Override
            protected Conversation doInvoke(String userId, Conversation.Message userMessage) {
                return new Conversation(Collections.singletonList(userMessage));
            }
        };
    }

    @Test
    @DisplayName("invoke returns result of doInvoke")
    void invokeReturnsDoInvokeResult() {
        // given
        Conversation.Message userMsg = new Conversation.Message("Hello", USER);
        // when
        Conversation result = actionAgent.invoke("user1", userMsg);
        // then
        assertEquals(1, result.messages().size());
        assertEquals("Hello", result.messages().getFirst().content());
    }

    @Nested
    class ConvertChatMemoryToConversationTests {
        @Test
        @DisplayName("convertChatMemoryToConversation returns correct Conversation for USER and ASSISTANT messages")
        void convertChatMemoryToConversationReturnsCorrectConversation() {
            // given
            UserMessage userMsg = new UserMessage("Hi");
            AssistantMessage assistantMsg = new AssistantMessage("Hello");
            List<Message> messages = Arrays.asList(userMsg, assistantMsg);
            when(chatMemory.get(ChatMemory.DEFAULT_CONVERSATION_ID)).thenReturn(messages);
            // when
            Conversation conversation = actionAgent.convertChatMemoryToConversation(chatMemory);
            // then
            assertEquals(2, conversation.messages().size());
            assertEquals("Hi", conversation.messages().get(0).content());
            assertEquals(USER, conversation.messages().get(0).sender());
            assertEquals("Hello", conversation.messages().get(1).content());
            assertEquals(ASSISTANT, conversation.messages().get(1).sender());
        }

        @Test
        @DisplayName("convertChatMemoryToConversation throws RuntimeException for unknown message type")
        void convertChatMemoryToConversationThrowsForUnknownType() {
            // given
            SystemMessage unknownMsg = new SystemMessage("Unknown");
            when(chatMemory.get(ChatMemory.DEFAULT_CONVERSATION_ID)).thenReturn(Collections.singletonList(unknownMsg));
            // when & then
            assertThrows(RuntimeException.class, () -> actionAgent.convertChatMemoryToConversation(chatMemory));
        }

        @Test
        @DisplayName("convertChatMemoryToConversation returns empty Conversation for empty chat memory")
        void convertChatMemoryToConversationReturnsEmptyForEmptyChatMemory() {
            // given
            when(chatMemory.get(ChatMemory.DEFAULT_CONVERSATION_ID)).thenReturn(Collections.emptyList());
            // when
            Conversation conversation = actionAgent.convertChatMemoryToConversation(chatMemory);
            // then
            assertTrue(conversation.messages().isEmpty());
        }
    }
}
