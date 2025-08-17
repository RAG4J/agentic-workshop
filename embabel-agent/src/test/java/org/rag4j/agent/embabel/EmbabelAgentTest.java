package org.rag4j.agent.embabel;

import com.embabel.agent.core.AgentPlatform;
import org.junit.jupiter.api.Test;
import org.rag4j.agent.core.Conversation;
import org.rag4j.agent.core.Sender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmbabelAgentTest {

    @Test
    void testInvokeWithValidMessage() {
        // Given
        AgentPlatform platform = mock(AgentPlatform.class);
        when(platform.getName()).thenReturn("Embabel");
        EmbabelAgent agent = new EmbabelAgent(platform);
        String userId = "test-user-123";
        Conversation.Message userMessage = new Conversation.Message("Hello there!", Sender.USER);

        // When
        Conversation result = agent.invoke(userId, userMessage);

        // Then
        assertNotNull(result);
        assertNotNull(result.messages());
        assertEquals(2, result.messages().size());

        // Verify user message is included
        Conversation.Message firstMessage = result.messages().get(0);
        assertEquals("Hello there!", firstMessage.content());
        assertEquals(Sender.USER, firstMessage.sender());

        // Verify assistant response
        Conversation.Message assistantMessage = result.messages().get(1);
        assertNotNull(assistantMessage.content());
        assertEquals(Sender.ASSISTANT, assistantMessage.sender());
        assertTrue(assistantMessage.content().contains("🌟"));
        assertTrue(assistantMessage.content().contains("Hello there!"));
        assertTrue(assistantMessage.content().contains("EmbabelAgent"));
    }

    @Test
    void testInvokeWithEmptyMessage() {
        // Given
        AgentPlatform platform = mock(AgentPlatform.class);
        EmbabelAgent agent = new EmbabelAgent(platform);
        String userId = "test-user-123";
        Conversation.Message userMessage = new Conversation.Message("", Sender.USER);

        // When
        Conversation result = agent.invoke(userId, userMessage);

        // Then
        assertNotNull(result);
        assertNotNull(result.messages());
        assertEquals(2, result.messages().size());

        // Verify assistant response for empty message
        Conversation.Message assistantMessage = result.messages().get(1);
        assertNotNull(assistantMessage.content());
        assertEquals(Sender.ASSISTANT, assistantMessage.sender());
        assertTrue(assistantMessage.content().contains("✨"));
        assertTrue(assistantMessage.content().contains("haven't said anything yet"));
    }

    @Test
    void testInvokeWithNullMessage() {
        // Given
        AgentPlatform platform = mock(AgentPlatform.class);
        EmbabelAgent agent = new EmbabelAgent(platform);
        String userId = "test-user-123";
        Conversation.Message userMessage = new Conversation.Message(null, Sender.USER);

        // When
        Conversation result = agent.invoke(userId, userMessage);

        // Then
        assertNotNull(result);
        assertNotNull(result.messages());
        assertEquals(2, result.messages().size());

        // Verify assistant response for null message
        Conversation.Message assistantMessage = result.messages().get(1);
        assertNotNull(assistantMessage.content());
        assertEquals(Sender.ASSISTANT, assistantMessage.sender());
        assertTrue(assistantMessage.content().contains("✨"));
        assertTrue(assistantMessage.content().contains("haven't said anything yet"));
    }
}
