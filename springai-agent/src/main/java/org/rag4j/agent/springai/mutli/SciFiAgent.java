package org.rag4j.agent.springai.mutli;

import java.util.ArrayList;
import java.util.List;

import org.rag4j.agent.core.Agent;
import org.rag4j.agent.core.Conversation;
import org.rag4j.agent.springai.ConferenceTalksTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;

import static org.rag4j.agent.core.Sender.*;

public class SciFiAgent implements Agent {
    private static final Logger logger = LoggerFactory.getLogger(SciFiAgent.class);
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public SciFiAgent(ChatClient chatClient, ChatMemory chatMemory) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
    }

    @Override
    public Conversation invoke(String userId, Conversation.Message userMessage) {
        logger.info("SciFiAgent invoke userId = {}, userMessage = {}", userId, userMessage);
        String content = this.chatClient.prompt()
                .tools(new ConferenceTalksTools())
                .system("""
                        You are a geek that knows everything about Science Fiction related topics and likes to answer questions about this.
                        Science Fiction is your only expertise, so you can not answer questions related to other topics.
                        If the question is about a non-scifi topic, just say you don't know anything about that subject.
                        """)
                .user(userMessage.content())
                .advisors(a -> a.param(ChatMemory.DEFAULT_CONVERSATION_ID, userId))
                .call()
                .content();
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
