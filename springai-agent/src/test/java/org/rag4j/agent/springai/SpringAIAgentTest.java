package org.rag4j.agent.springai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class SpringAIAgentTest {

    @Test
    @DisplayName("SpringAIAgent can be created with required dependencies")
    void springAIAgentCanBeCreatedWithRequiredDependencies() {
        ChatClient chatClient = mock(ChatClient.class);
        ConferenceTalksTools conferenceTalksTools = mock(ConferenceTalksTools.class);
        SpringAIAgent agent = new SpringAIAgent(chatClient, conferenceTalksTools);
        
        assertNotNull(agent);
    }
}
