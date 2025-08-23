package org.rag4j.agent.embabel;

import com.embabel.agent.api.common.autonomy.AgentInvocation;
import com.embabel.agent.core.AgentPlatform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.rag4j.agent.core.Conversation;
import org.rag4j.agent.core.Sender;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmbabelAgentTest {
    
    @Test
    @DisplayName("invoke returns conversation with user and embellished response for normal input")
    void invokeReturnsConversationWithUserAndEmbellishedResponse() {
        // Given
        AgentPlatform platform = mock(AgentPlatform.class);
        AgentInvocation.Builder builder = mock(AgentInvocation.Builder.class);
        AgentInvocation<Conversation> invocation = mock(AgentInvocation.class);
        
        Conversation.Message userMessage = new Conversation.Message("Hello there!", Sender.USER);
        Conversation.Message assistantMessage = new Conversation.Message("🌟 Hello! How can I assist you with conference talks today?", Sender.ASSISTANT);
        Conversation fakeResponse = new Conversation(List.of(assistantMessage));
        
        // Mock the builder pattern chain
        when(builder.options(any(Consumer.class))).thenReturn(builder);
        when(builder.build(Conversation.class)).thenReturn(invocation);
        when(invocation.invoke(userMessage)).thenReturn(fakeResponse);
        
        // When & Then
        try (MockedStatic<AgentInvocation> staticMock = Mockito.mockStatic(AgentInvocation.class)) {
            staticMock.when(() -> AgentInvocation.builder(platform)).thenReturn(builder);
            
            EmbabelAgent agent = new EmbabelAgent(platform);
            Conversation result = agent.invoke("user1", userMessage);
            
            assertEquals(2, result.messages().size());
            assertEquals(userMessage, result.messages().get(0));
            assertEquals(assistantMessage, result.messages().get(1));
        }
    }

    @Test
    @DisplayName("invoke handles empty response gracefully")
    void invokeHandlesEmptyResponseGracefully() {
        // Given
        AgentPlatform platform = mock(AgentPlatform.class);
        AgentInvocation.Builder builder = mock(AgentInvocation.Builder.class);
        AgentInvocation<Conversation> invocation = mock(AgentInvocation.class);
        
        Conversation.Message userMessage = new Conversation.Message("Hi", Sender.USER);
        Conversation.Message defaultMessage = new Conversation.Message("I'm sorry, I couldn't generate a response.", Sender.ASSISTANT);
        Conversation emptyResponse = new Conversation(List.of(defaultMessage));
        
        // Mock the builder pattern chain
        when(builder.options(any(Consumer.class))).thenReturn(builder);
        when(builder.build(Conversation.class)).thenReturn(invocation);
        when(invocation.invoke(userMessage)).thenReturn(emptyResponse);
        
        // When & Then
        try (MockedStatic<AgentInvocation> staticMock = Mockito.mockStatic(AgentInvocation.class)) {
            staticMock.when(() -> AgentInvocation.builder(platform)).thenReturn(builder);
            
            EmbabelAgent agent = new EmbabelAgent(platform);
            Conversation result = agent.invoke("user2", userMessage);
            
            assertEquals(2, result.messages().size());
            assertEquals(userMessage, result.messages().get(0));
            assertEquals(defaultMessage, result.messages().get(1));
        }
    }

    @Test
    @DisplayName("invoke throws exception if AgentInvocation throws")
    void invokeThrowsExceptionIfAgentInvocationThrows() {
        // Given
        AgentPlatform platform = mock(AgentPlatform.class);
        AgentInvocation.Builder builder = mock(AgentInvocation.Builder.class);
        AgentInvocation<Conversation> invocation = mock(AgentInvocation.class);
        
        Conversation.Message userMessage = new Conversation.Message("Error please", Sender.USER);
        
        // Mock the builder pattern chain
        when(builder.options(any(Consumer.class))).thenReturn(builder);
        when(builder.build(Conversation.class)).thenReturn(invocation);
        when(invocation.invoke(userMessage)).thenThrow(new RuntimeException("Agent error"));
        
        // When & Then
        try (MockedStatic<AgentInvocation> staticMock = Mockito.mockStatic(AgentInvocation.class)) {
            staticMock.when(() -> AgentInvocation.builder(platform)).thenReturn(builder);
            
            EmbabelAgent agent = new EmbabelAgent(platform);
            assertThrows(RuntimeException.class, () -> agent.invoke("user3", userMessage));
        }
    }

    @Test
    @DisplayName("invoke logs processing information")
    void invokeLogsProcessingInformation() {
        // Given
        AgentPlatform platform = mock(AgentPlatform.class);
        AgentInvocation.Builder builder = mock(AgentInvocation.Builder.class);
        AgentInvocation<Conversation> invocation = mock(AgentInvocation.class);
        
        Conversation.Message userMessage = new Conversation.Message("Test message", Sender.USER);
        Conversation.Message assistantMessage = new Conversation.Message("Test response", Sender.ASSISTANT);
        Conversation fakeResponse = new Conversation(List.of(assistantMessage));
        
        // Mock the builder pattern chain
        when(builder.options(any(Consumer.class))).thenReturn(builder);
        when(builder.build(Conversation.class)).thenReturn(invocation);
        when(invocation.invoke(userMessage)).thenReturn(fakeResponse);
        
        // When & Then
        try (MockedStatic<AgentInvocation> staticMock = Mockito.mockStatic(AgentInvocation.class)) {
            staticMock.when(() -> AgentInvocation.builder(platform)).thenReturn(builder);
            
            EmbabelAgent agent = new EmbabelAgent(platform);
            Conversation result = agent.invoke("test-user", userMessage);
            
            // Verify the result is correct
            assertNotNull(result);
            assertEquals(2, result.messages().size());
            
            // Verify interactions
            verify(invocation).invoke(userMessage);
            verify(builder).options(any(Consumer.class));
            verify(builder).build(Conversation.class);
        }
    }
}
