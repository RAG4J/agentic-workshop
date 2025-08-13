package org.rag4j.agent;

import org.rag4j.agent.core.Agent;
import org.rag4j.agent.core.ConferenceTalk;
import org.rag4j.agent.core.ConferenceTalksRepository;
import org.rag4j.agent.core.Conversation;
import org.rag4j.agent.memory.Memory;
import org.rag4j.agent.reasoning.Reasoning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.rag4j.agent.core.Sender.ASSISTANT;
import static org.rag4j.agent.core.Sender.OBSERVATION;

/**
 *
 */
public class PlainJavaAgent implements Agent {
    private static final Logger logger = LoggerFactory.getLogger(PlainJavaAgent.class);
    private final Reasoning reasoning;
    private final Memory memory;
    private final int maxReasoningSteps;
    private final ConferenceTalksRepository conferenceTalksRepository;

    public PlainJavaAgent(Reasoning reasoning, Memory memory, int maxReasoningSteps, ConferenceTalksRepository conferenceTalksRepository) {
        this.reasoning = reasoning;
        this.memory = memory;
        this.maxReasoningSteps = maxReasoningSteps;
        this.conferenceTalksRepository = conferenceTalksRepository;
    }


    public Conversation invoke(String userId, Conversation.Message message) {
        Conversation conversation = memory.retrieveConversation(userId);

        Conversation.Message answerMessage = this.callReasoning(message, conversation, 1);

        conversation.messages().add(message);
        conversation.messages().add(answerMessage);
        memory.storeConversation(userId, conversation);

        return conversation;
    }

    private Conversation.Message callReasoning(Conversation.Message userMessage, Conversation conversation, int reasoningStep) {
        if (reasoningStep > maxReasoningSteps) {
            logger.warn("Max reasoning steps reached, returning conversation without answer.");
            return new Conversation.Message("Unable to provide an answer after multiple reasoning steps.",
                    ASSISTANT);
        }

        // Call the reasoning service to get the response
        Conversation.Message response = reasoning.reason(userMessage, conversation);
        logger.debug("Received response: {}", response.content());

        // Log thinking and extract answer or action
        logThinking(response.content());
        Optional<String> answer = extractAnswer(response.content());
        if (answer.isPresent()) {
            String answerText = answer.get();
            logger.info("Answer: {}", answerText);
            return new Conversation.Message(answerText, ASSISTANT);
        }

        Optional<AgentAction> action = extractAction(response.content());
        if (action.isPresent()) {
            AgentAction agentAction = action.get();
            logger.info("Action: {} with arguments: {}", agentAction.actionName(), agentAction.arguments());
            String actionResponse = this.executeAction(agentAction);
            Conversation.Message observationMessage = new Conversation.Message(
                    "Observation: " + actionResponse, OBSERVATION);
            return this.callReasoning(observationMessage, conversation, reasoningStep + 1);
        }

        return new Conversation.Message(
                "The Agent could not create an answer to your question.", ASSISTANT);
    }

    private String executeAction(AgentAction action) {
        // Here you would implement the logic to execute the action
        // For now, we will just return a dummy response
        if (action.actionName().equals("get_talk_by_name")) {
            String title = extractName(action.arguments());
            logger.info("Finding talk by title: {}", title);
            List<ConferenceTalk> talksByTitle = this.conferenceTalksRepository.findTalksByTitle(title);
            if (talksByTitle.isEmpty()) {
                return "No talks found with the title: " + title;
            }
            // Create a string containing all the talks found with a short message telling these are the found talks
            StringBuilder response = new StringBuilder("Found talks with title '" + title + "':\n");
            for (ConferenceTalk talk : talksByTitle) {
                response.append(talk.toString());
                response.append("\n");
            }
            return response.toString();
        } else if (action.actionName().equals("get_talk_by_speaker")) {
            String speaker = extractName(action.arguments());
            logger.info("Finding talk by author: {}", speaker);
            List<ConferenceTalk> talksBySpeaker = this.conferenceTalksRepository.findTalksBySpeaker(speaker);
            if (talksBySpeaker.isEmpty()) {
                return "No talks found with the title: " + speaker;
            }
            // Create a string containing all the talks found with a short message telling these are the found talks
            StringBuilder response = new StringBuilder("Found talks with speaker '" + speaker + "':\n");
            for (ConferenceTalk talk : talksBySpeaker) {
                response.append(talk.toString());
                response.append("\n");
            }
            return response.toString();
        }
        return "Unknown action executed.";
    }

    private static String extractName(String arguments) {
        Pattern pattern = Pattern.compile("\\{\"name\":\\s*\"([^\"]+)\"\\}");
        Matcher matcher = pattern.matcher(arguments);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static void logThinking(String output_message) {
        Pattern thinkPattern = Pattern.compile("^Think: (.*)$", Pattern.MULTILINE);
        Matcher thinkMatcher = thinkPattern.matcher(output_message);
        while (thinkMatcher.find()) {
            String thought = thinkMatcher.group(1);
            logger.info("Think: {}", thought);
        }
    }

    private static Optional<String> extractAnswer(String output_message) {
        Pattern answerPattern = Pattern.compile("^Answer: (.*)$", Pattern.MULTILINE);
        Matcher answerMatcher = answerPattern.matcher(output_message);
        if (answerMatcher.find()) {
            String answer = answerMatcher.group(1);
            return Optional.of(answer.trim());
        }
        return Optional.empty();
    }

    private static Optional<AgentAction> extractAction(String output_message) {
        Pattern actionPattern = Pattern.compile("^Action: (\\w+): (.*)$", Pattern.MULTILINE);
        Matcher actionMatcher = actionPattern.matcher(output_message);
        if (actionMatcher.find()) {
            String actionName = actionMatcher.group(1);
            String arguments = actionMatcher.group(2).trim();
            return Optional.of(new AgentAction(actionName, arguments));
        }
        return Optional.empty();
    }

    public record AgentAction(String actionName, String arguments) {
    }
}
