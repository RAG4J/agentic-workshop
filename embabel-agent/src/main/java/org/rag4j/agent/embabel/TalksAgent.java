package org.rag4j.agent.embabel;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.config.models.OpenAiModels;
import org.rag4j.agent.core.Conversation;
import org.rag4j.agent.embabel.model.TimeRequest;
import org.rag4j.agent.embabel.model.TimeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Agent(name = "TalksAgent",
        description = "An agent that answers questions about conference talks and current time across the world using the provided tools.",
        version = "1.0.0")
public record TalksAgent(EmbabelConferenceTools tools) {
    private static final Logger logger = LoggerFactory.getLogger(TalksAgent.class);

    @AchievesGoal(
            description = "Answers a question about conference talks using tools to obtain the right talks. For questions about current time, use another action."
    )
    @Action
    public Conversation answerQuestion(Conversation.Message question, OperationContext context) {
        Conversation response = context.ai().withLlm(OpenAiModels.GPT_41_MINI)
                .withToolObject(tools)
                .createObject(String.format("""
                                 You will be given a question about conference talks.
                                 You have access to talks to search for conference talks.
                                 Your task is to answer the question using the information from the talks.
                                
                                 # Question
                                 %s
                                
                                """,
                        question.content()
                ).trim(), Conversation.class);
        logger.info("Response generated: {}", response.messages().getFirst().content());
        return response;
    }

    @Action(toolGroups = {"mcp-time"})
    public TimeResponse timeQuestion(TimeRequest timeRequest, OperationContext context) {
        TimeResponse response = context.ai().withLlm(OpenAiModels.GPT_41_MINI)
                .withToolObject(tools)
                .createObject(String.format("""
                         You will be given a request related to time.
                         You have access to tools to get the current time.
                         Your task is to use the tools to get the current time and return it.
                        
                         # Request
                            Region: %s
                            Question: %s
                        
                        """, timeRequest.region(), timeRequest.timeQuestion()).trim(), TimeResponse.class);
        logger.info("Current time response generated: {}", response.currentTime());
        return response;
    }

}
