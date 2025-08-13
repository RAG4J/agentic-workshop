package org.rag4j.agent.core;

import java.util.List;

public record ConferenceTalk(
        String title,
        String description,
        String startTime,
        String room,
        List<Speaker> speakers
) {
}
