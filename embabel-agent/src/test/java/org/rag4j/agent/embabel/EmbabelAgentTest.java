package org.rag4j.agent.embabel;

import com.embabel.agent.core.Agent;
import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.core.AgentProcess;
import com.embabel.agent.core.ProcessOptions;
import org.junit.jupiter.api.Test;
import org.rag4j.agent.core.Conversation;
import org.rag4j.agent.core.Sender;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmbabelAgentTest {

    @Test
    void testInvokeWithValidMessage() {
        // Given
        AgentPlatform platform = mock(AgentPlatform.class);
        Agent embabelAgent = mock(Agent.class);
        AgentProcess process = mock(AgentProcess.class);
        when(embabelAgent.getName()).thenReturn("talks");
        when(platform.getName()).thenReturn("Embabel");
        when(platform.agents()).thenReturn(Collections.singletonList(embabelAgent));

        Conversation.Message userMessage = new Conversation.Message("Hello there!", Sender.USER);
        when(platform.createAgentProcessFrom(eq(embabelAgent), any(ProcessOptions.class), eq(userMessage)))
                .thenReturn(process);
        Conversation.Message assistant = new Conversation.Message("🌟 Hello there! — responded by EmbabelAgent", Sender.ASSISTANT);
        when(process.lastResult()).thenReturn(new Conversation(java.util.List.of(assistant)));
        when(platform.start(process)).thenReturn(CompletableFuture.completedFuture(process));

        EmbabelAgent agent = new EmbabelAgent(platform);
        String userId = "test-user-123";

        // When
        Conversation result = agent.invoke(userId, userMessage);

        // Then
        assertNotNull(result);
        assertNotNull(result.messages());
        assertEquals(2, result.messages().size());

        // Verify user message is included
        Conversation.Message firstMessage = result.messages().getFirst();
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
        Agent embabelAgent = mock(Agent.class);
        AgentProcess process = mock(AgentProcess.class);
        when(embabelAgent.getName()).thenReturn("talks");
        when(platform.getName()).thenReturn("Embabel");
        when(platform.agents()).thenReturn(Collections.singletonList(embabelAgent));

        Conversation.Message userMessage = new Conversation.Message("", Sender.USER);
        when(platform.createAgentProcessFrom(eq(embabelAgent), any(ProcessOptions.class), eq(userMessage)))
                .thenReturn(process);
        Conversation.Message assistant = new Conversation.Message("✨ It seems you haven't said anything yet.", Sender.ASSISTANT);
        when(process.lastResult()).thenReturn(new Conversation(java.util.List.of(assistant)));
        when(platform.start(process)).thenReturn(CompletableFuture.completedFuture(process));

        EmbabelAgent agent = new EmbabelAgent(platform);
        String userId = "test-user-123";

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
        assertTrue(assistantMessage.content().contains("✨") || assistantMessage.content().contains("🌟"));
        assertTrue(assistantMessage.content().toLowerCase().contains("haven't said anything yet") || assistantMessage.content().toLowerCase().contains("no input provided"));
    }

    @Test
    void testInvokeWithNullMessage() {
        // Given
        AgentPlatform platform = mock(AgentPlatform.class);
        Agent embabelAgent = mock(Agent.class);
        AgentProcess process = mock(AgentProcess.class);
        when(embabelAgent.getName()).thenReturn("talks");
        when(platform.getName()).thenReturn("Embabel");
        when(platform.agents()).thenReturn(Collections.singletonList(embabelAgent));

        Conversation.Message userMessage = new Conversation.Message(null, Sender.USER);
        when(platform.createAgentProcessFrom(eq(embabelAgent), any(ProcessOptions.class), eq(userMessage)))
                .thenReturn(process);
        Conversation.Message assistant = new Conversation.Message("🌟 No input provided.", Sender.ASSISTANT);
        when(process.lastResult()).thenReturn(new Conversation(java.util.List.of(assistant)));
        when(platform.start(process)).thenReturn(CompletableFuture.completedFuture(process));

        EmbabelAgent agent = new EmbabelAgent(platform);
        String userId = "test-user-123";

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
        assertTrue(assistantMessage.content().contains("✨") || assistantMessage.content().contains("🌟"));
        assertTrue(assistantMessage.content().toLowerCase().contains("haven't said anything yet") || assistantMessage.content().toLowerCase().contains("no input provided"));
    }
}
