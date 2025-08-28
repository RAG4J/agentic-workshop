package org.rag4j.agent.embabel;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.config.models.OpenAiModels;
import com.embabel.agent.domain.io.UserInput;
import org.rag4j.agent.core.Conversation;
import org.rag4j.agent.embabel.model.TimeRequest;
import org.rag4j.agent.embabel.model.TimeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Agent(
        name = "TimeAgent",
        description = "An agent that answers questions about current time across the world using the provided tools.",
        version = "1.0.0"
)
public class TimeAgent {
    private static final Logger logger = LoggerFactory.getLogger(TimeAgent.class);

    @Action(description = "Extracts time request details from user input.")
    public TimeRequest extractTimeRequest(UserInput userInput, OperationContext context) {
        TimeRequest response = context.ai().withLlm(OpenAiModels.GPT_41_MINI)
                .createObject(String.format("""
                         You will be given a question from a user.
                         Your task is to transform the question into a time request object.
                        
                         # Request
                            Question: %s
                        
                        """, userInput.getContent()).trim(), TimeRequest.class);
        logger.info("Extracted time related question: {}", response.timeQuestion());
        return response;
    }

    @Action(toolGroups = {"mcp-time"})
    public TimeResponse timeQuestion(TimeRequest timeRequest, OperationContext context) {
        TimeResponse response = context.ai().withLlm(OpenAiModels.GPT_41_MINI)
                .createObject(String.format("""
                         You will be given a request related to time.
                         You have access to tools to get the current time or other time related questions.
                         Your task is to use the tools to answer time related questions.
                        
                         # Request
                            Region: %s
                            Question: %s
                        
                        """, timeRequest.region(), timeRequest.timeQuestion()).trim(), TimeResponse.class);
        logger.info("Answer for the time related question: {}", response.timeAnswer());
        return response;
    }

    @AchievesGoal(
            description = "Answer a question about time in a conversation manner."
    )
    @Action
    public Conversation startTimeConversation(TimeResponse timeResponse, TimeRequest timeRequest, OperationContext context) {
        Conversation response = context.ai().withLlm(OpenAiModels.GPT_41_MINI)
                .createObject(String.format("""
                         You will be given a time related answer and the original question.
                         Your task is to create a conversation message that provides the answer to the user in a friendly manner.
                        
                         # Original Question
                            %s
                        
                         # Answer
                            %s
                        
                        """, timeRequest.timeQuestion(), timeResponse.timeAnswer()).trim(), Conversation.class);
        logger.info("Generated time related conversation message: {}", response.messages().getFirst().content());
        return response;
    }

}
