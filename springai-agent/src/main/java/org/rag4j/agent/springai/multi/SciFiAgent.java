package org.rag4j.agent.springai.multi;

import java.util.List;

import org.rag4j.agent.core.Conversation;
import org.rag4j.agent.springai.ActionAgent;
import org.rag4j.agent.springai.advisor.PromptInjectionGuardAdvisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;

public class SciFiAgent extends ActionAgent {
    private static final Logger logger = LoggerFactory.getLogger(SciFiAgent.class);

    public SciFiAgent(ChatClient chatClient, ChatMemory chatMemory) {
        super(chatClient, chatMemory);
    }

    @Override
    public Conversation doInvoke(String userId, Conversation.Message userMessage) {
        logger.info("SciFiAgent invoke userId = {}, userMessage = {}", userId, userMessage);
        String content = this.chatClient.prompt()
                .advisors(
                        SafeGuardAdvisor.builder()
                            .sensitiveWords(List.of("darth vader", "star wars"))
                            .failureResponse("Blocked due to IP restrictions").build(),
                        MessageChatMemoryAdvisor.builder(chatMemory).conversationId(userId).build()
                )
                .system("""
                        You are a geek that knows everything about Science Fiction related topics and likes to answer questions about this.
                        Science Fiction is your only expertise, so you can not answer questions related to other topics.
                        If the question is about a non-scifi topic, just say you don't know anything about that subject.
                        """)
                .user(userMessage.content())
                .call()
                .content();
        logger.info("SpringAIAgent invoke content = {}", content);
        return convertChatMemoryToConversation(userId, chatMemory);
    }
}
