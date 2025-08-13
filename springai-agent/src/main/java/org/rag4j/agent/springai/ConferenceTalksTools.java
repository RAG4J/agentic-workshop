package org.rag4j.agent.springai;

import org.rag4j.agent.core.ConferenceTalk;
import org.rag4j.agent.core.Speaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;

import java.util.List;

public class ConferenceTalksTools {
    private static final Logger logger = LoggerFactory.getLogger(ConferenceTalksTools.class);

    @Tool(description = "Find a conference talk by its title.")
    public ConferenceTalk findConferenceTalkByTitle(String title) {
        logger.info("Finding conference talk by title: {}", title);

        return new ConferenceTalk(
            "Introduction to Spring AI",
            "Learn how to build AI applications with Spring AI.",
            "10:00",
            "3",
            List.of(new Speaker("Jettro Coenradie"), new Speaker("Daniel Spee"))
        );
    }

    @Tool(description = "Find all conference talks by a specific speaker.")
    public List<ConferenceTalk> findConferenceTalksBySpeaker(String speakerName) {
        logger.info("Finding conference talks by speaker: {}", speakerName);

        return List.of(
            new ConferenceTalk(
                "Introduction to Spring AI",
                "Learn how to build AI applications with Spring AI.",
                "10:00",
                "3",
                List.of(new Speaker(speakerName))
            ),
            new ConferenceTalk(
                "Advanced Spring AI Techniques",
                "Deep dive into advanced features of Spring AI.",
                "11:00",
                "4",
                List.of(new Speaker(speakerName))
            )
        );
    }
}
