package org.rag4j.agent.springai.multi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Set<String> getAvailableAgents(String userId) {
        if (userId.endsWith("scifi")) {
            return registeredAgents.keySet().stream().filter(agentName -> agentName.contains("Geek")).collect(Collectors.toSet());
        } else if  (userId.endsWith("talks")) {
            return registeredAgents.keySet().stream().filter(agentName -> agentName.contains("Talks")).collect(Collectors.toSet());
        } else {
            return registeredAgents.keySet();
        }
    }
}
