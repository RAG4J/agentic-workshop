package org.rag4j.agent.springai;

import org.rag4j.agent.core.Conversation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;

/**
 * Spring AI-based implementation of the Agent interface.
 * Uses Spring AI's ChatClient for LLM interactions.
 */
public class TalksAgent extends ActionAgent {
    private static final Logger logger = LoggerFactory.getLogger(TalksAgent.class);
    private final ConferenceTalksTools conferenceTalksTools;

    public TalksAgent(ChatClient chatClient, ChatMemory chatMemory, ConferenceTalksTools conferenceTalksTools) {
        super(chatClient, chatMemory);
        this.conferenceTalksTools = conferenceTalksTools;
    }

    @Override
    protected Conversation doInvoke(String userId, Conversation.Message userMessage) {
        logger.info("SpringAIAgent invoke userId = {}, userMessage = {}", userId, userMessage);
        String content = this.chatClient.prompt()
                .tools(conferenceTalksTools)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(userId).build())
                .system("You are an AI agent that answers questions about conference talks. Do not answer generic questions, even if you know the answers. Stick to information about conferences and the program that is available through your tools.")
                .user(userMessage.content())
                .advisors(a -> a.param(ChatMemory.DEFAULT_CONVERSATION_ID, userId))
                .call()
                .content();
        assert content != null;
        logger.info("SpringAIAgent invoke content = {}", content);
        return convertChatMemoryToConversation(userId, chatMemory);
    }
}
