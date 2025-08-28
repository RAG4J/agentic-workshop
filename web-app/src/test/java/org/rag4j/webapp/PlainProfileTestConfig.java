package org.rag4j.webapp;

import org.rag4j.agent.core.ConferenceTalksRepository;
import org.rag4j.agent.embabel.EmbabelConferenceTools;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class PlainProfileTestConfig {

    @Bean
    @Profile("plain")
    public EmbabelConferenceTools testEmbabelConferenceTools(ConferenceTalksRepository conferenceTalksRepository) {
        return new EmbabelConferenceTools(conferenceTalksRepository);
    }
}
