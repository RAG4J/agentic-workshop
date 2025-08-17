package org.rag4j.agent.springai.mutli;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.rag4j.agent.core.Agent;

public class AgentRegistry {
    private final Map<String, Agent> registeredAgents = new HashMap<>();

    public Agent getAgent(String id) {
        return registeredAgents.get(id);
    }
    public void registerAgent(String name, Agent agent) {
        registeredAgents.put(name, agent);
    }

    public Set<String> getAvailableAgents() {
        return registeredAgents.keySet();
    }
}
