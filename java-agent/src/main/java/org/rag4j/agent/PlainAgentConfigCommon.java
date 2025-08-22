package org.rag4j.agent;

import org.rag4j.agent.core.ConferenceTalksRepository;
import org.rag4j.agent.memory.Memory;
import org.rag4j.agent.memory.WindowedConversationMemory;
import org.rag4j.agent.tools.FindTalksBySpeaker;
import org.rag4j.agent.tools.FindTalksByTitle;
import org.rag4j.agent.tools.Tool;
import org.rag4j.agent.tools.ToolRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile({"plain","plain-multi"})
public class PlainAgentConfigCommon {
    @Bean
    public ConferenceTalksRepository conferenceTalksRepository() {
        return new ConferenceTalksRepository();
    }

    @Bean
    public Memory memory(@Value("${agent.plain.conversation.max-size:10}") int maxConversationSize) {
        return new WindowedConversationMemory(maxConversationSize);
    }

    @Bean(name = "toolRegistry")
    public ToolRegistry toolRegistry(ConferenceTalksRepository conferenceTalksRepository) {
        List<Tool> tools = List.of(
                new FindTalksByTitle(conferenceTalksRepository),
                new FindTalksBySpeaker(conferenceTalksRepository)
        );
        return new ToolRegistry(tools);
    }
}
