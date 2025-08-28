package org.rag4j.agent.embabel;

import com.embabel.agent.api.common.autonomy.Autonomy;
import com.embabel.agent.config.annotation.EnableAgents;
import com.embabel.agent.config.annotation.LoggingThemes;
import com.embabel.agent.config.annotation.McpServers;
import com.embabel.agent.core.AgentPlatform;
import org.rag4j.agent.core.Agent;
import org.rag4j.agent.core.ConferenceTalksRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("embabel")
@EnableAgents(loggingTheme = LoggingThemes.STAR_WARS, mcpServers = McpServers.DOCKER)
public class EmbabelAgentConfig {

    @Bean
    public ConferenceTalksRepository getConferenceTalksRepository() {
        return new ConferenceTalksRepository();
    }

    @Bean(name = "orchestrator")
    public Agent embabelAgent(AgentPlatform platform, Autonomy autonomy) {
        return new EmbabelAgent(platform, autonomy);
    }

    @Bean
    public EmbabelConferenceTools embabelConferenceTools(ConferenceTalksRepository conferenceTalksRepository) {
        return new EmbabelConferenceTools(conferenceTalksRepository);
    }
}
