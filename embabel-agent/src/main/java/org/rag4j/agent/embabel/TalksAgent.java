package org.rag4j.agent.embabel;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.config.models.OpenAiModels;
import org.rag4j.agent.core.Conversation;
import org.rag4j.agent.core.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Agent(name = "TalksAgent",
        description = "An agent that answers questions about conference talks using the provided tools.",
        version = "1.0.0")
public record TalksAgent(EmbabelConferenceTools tools) {
    private static final Logger logger = LoggerFactory.getLogger(TalksAgent.class);

    @AchievesGoal(
            description = "Answers a question about conference talks using tools to obtain the right talks."
    )
    @Action
    public Conversation answerQuestion(Conversation.Message question, OperationContext context) {
        String response = context.ai().withLlm(OpenAiModels.GPT_41_MINI)
                .withToolObject(tools)
                .generateText(String.format("""
                                 You will be given a question about conference talks.
                                 You have access to talks to search for conference talks.
                                 Your task is to answer the question using the information from the talks.
                                
                                 # Question
                                 %s
                                
                                """,
                        question.content()
                ).trim());
        logger.info("Response generated: {}", response);
        return new Conversation(List.of(new Conversation.Message(response, Sender.ASSISTANT)));
    }

}
