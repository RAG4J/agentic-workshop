package org.rag4j.evals.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Dummy AgentRunner service that generates responses to questions.
 * This is a placeholder implementation to be replaced with actual agent logic.
 */
@Service
public class AgentRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentRunner.class);
    private final Random random = new Random();
    
    // Dummy responses for demonstration
    private static final List<String> DUMMY_RESPONSES = List.of(
        "Based on my analysis of the available information, I can provide the following response: This appears to be related to conference talks and presentations.",
        "After reviewing the relevant data, I believe this question pertains to speaker information and talk topics in our conference database.",
        "According to my understanding of the context, this seems to involve AI-related presentations and development topics.",
        "From the information available, I can see this relates to technical talks, particularly around development tools and methodologies.",
        "This question appears to be asking about specific speakers and their areas of expertise in the conference lineup.",
        "Based on the pattern of questions, this seems to involve categorizing talks by topic and identifying relevant speakers.",
        "My analysis suggests this is related to finding talks within specific technical domains or subject areas."
    );
    
    /**
     * Generates a response to the given question.
     * This is a dummy implementation that returns mock responses.
     * 
     * @param question The input question to process
     * @return A generated response (currently dummy data)
     */
    public String generateResponse(String question) {
        logger.info("Processing question: {}", question);
        
        // Simulate some processing time
        try {
            Thread.sleep(100 + random.nextInt(400)); // 100-500ms delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Interrupted while processing question", e);
        }
        
        // Select a random dummy response
        String response = DUMMY_RESPONSES.get(random.nextInt(DUMMY_RESPONSES.size()));
        
        // Add some question-specific context to make it more realistic
        String contextualResponse = addContextualElements(question, response);
        
        logger.info("Generated response for question '{}': {}", 
                   question.length() > 50 ? question.substring(0, 50) + "..." : question,
                   contextualResponse.length() > 100 ? contextualResponse.substring(0, 100) + "..." : contextualResponse);
        
        return contextualResponse;
    }
    
    /**
     * Adds contextual elements to the response based on the question content.
     */
    private String addContextualElements(String question, String baseResponse) {
        String lowerQuestion = question.toLowerCase();
        
        // Add specific context based on question keywords
        if (lowerQuestion.contains("speaker") || lowerQuestion.contains("who")) {
            return baseResponse + " The speaker information should be available in our conference database.";
        } else if (lowerQuestion.contains("how many") || lowerQuestion.contains("count")) {
            return baseResponse + " I would need to count the relevant entries to provide an accurate number.";
        } else if (lowerQuestion.contains("what") && (lowerQuestion.contains("talk") || lowerQuestion.contains("speak"))) {
            return baseResponse + " This involves analyzing talk topics and abstracts for relevant content.";
        } else if (lowerQuestion.contains("development") || lowerQuestion.contains("ide")) {
            return baseResponse + " This relates to development tools and programming environments.";
        } else if (lowerQuestion.contains("challenge") || lowerQuestion.contains("coding")) {
            return baseResponse + " Programming challenges and coding exercises are common conference topics.";
        } else {
            return baseResponse + " I'll need to search through the available data to find the most relevant information.";
        }
    }
    
    /**
     * Generates responses for a batch of questions.
     * This can be useful for processing multiple questions efficiently.
     * 
     * @param questions List of questions to process
     * @return List of responses in the same order as the questions
     */
    public List<String> generateResponses(List<String> questions) {
        logger.info("Processing batch of {} questions", questions.size());
        
        return questions.stream()
                .map(this::generateResponse)
                .toList();
    }
    
    /**
     * Checks if the agent runner is available and ready to process questions.
     * In a real implementation, this might check API connectivity, model availability, etc.
     * 
     * @return true if the agent is ready, false otherwise
     */
    public boolean isReady() {
        // For the dummy implementation, always return true
        return true;
    }
    
    /**
     * Gets information about the current agent configuration.
     * This is useful for debugging and monitoring.
     * 
     * @return A string describing the current agent setup
     */
    public String getAgentInfo() {
        return "Dummy AgentRunner v1.0 - Mock responses enabled";
    }
}
