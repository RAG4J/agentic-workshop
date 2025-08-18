package org.rag4j.agent.springai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rag4j.agent.core.Agent;
import org.rag4j.agent.core.ConferenceTalksRepository;
import org.rag4j.agent.core.Conversation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;

import static org.rag4j.agent.core.Sender.*;

/**
 * Spring AI-based implementation of the Agent interface.
 * Uses Spring AI's ChatClient for LLM interactions.
 */
public class SpringAIAgent implements Agent {
    private static final Logger logger = LoggerFactory.getLogger(SpringAIAgent.class);
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public SpringAIAgent(ChatClient chatClient, ChatMemory chatMemory) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
    }

    @Override
    public Conversation invoke(String userId, Conversation.Message userMessage) {
        logger.info("SpringAIAgent invoke userId = {}, userMessage = {}", userId, userMessage);
        String content = this.chatClient.prompt()
                .tools(new ConferenceTalksTools(new ConferenceTalksRepository()))
                .system("You are an AI agent that answers questions about conference talks.")
                .user(userMessage.content())
                .advisors(a -> a.param(ChatMemory.DEFAULT_CONVERSATION_ID, userId))
                .call()
                .content();
        assert content != null;
        logger.info("SpringAIAgent invoke content = {}", content);
        return convertChatMemoryToConversation(chatMemory);
    }

    private Conversation convertChatMemoryToConversation(ChatMemory chatMemory) {
        List<Conversation.Message> messages = new ArrayList<>();
        for  (Message message : chatMemory.get(ChatMemory.DEFAULT_CONVERSATION_ID)) {
            switch (message.getMessageType()) {
                case MessageType.USER:
                    messages.add(new Conversation.Message(message.getText(), USER));
                    break;
                case MessageType.ASSISTANT:
                    messages.add(new Conversation.Message(message.getText(), ASSISTANT));
                    break;
                default:
                    throw new RuntimeException("Unknown message type: " + message.getMessageType());
            }
        }
        return new Conversation(messages);
    }
}
