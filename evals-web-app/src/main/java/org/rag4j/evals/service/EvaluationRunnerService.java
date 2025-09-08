package org.rag4j.evals.service;

import org.rag4j.evals.model.EvaluationRecord;
import org.rag4j.evals.model.EvaluationRun;
import org.rag4j.evals.model.RunStatus;
import org.rag4j.evals.model.ScoreType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service responsible for creating and executing evaluation runs with questions and agent responses
 */
@Service
public class EvaluationRunnerService {
    
    private static final Logger logger = LoggerFactory.getLogger(EvaluationRunnerService.class);
    
    private final QuestionLoaderService questionLoaderService;
    private final AgentRunner agentRunner;
    private final EvaluationDataService evaluationDataService;
    
    @Autowired
    public EvaluationRunnerService(QuestionLoaderService questionLoaderService,
                                 AgentRunner agentRunner,
                                 EvaluationDataService evaluationDataService) {
        this.questionLoaderService = questionLoaderService;
        this.agentRunner = agentRunner;
        this.evaluationDataService = evaluationDataService;
    }
    
    /**
     * Creates a new evaluation run with records generated from input questions
     * 
     * @param run The evaluation run to create
     * @return The created run with updated statistics
     * @throws IOException if questions cannot be loaded
     */
    public EvaluationRun createRunWithQuestions(EvaluationRun run) throws IOException {
        logger.info("Creating evaluation run with questions: {}", run.getName());
        
        // Load questions from the input file
        List<String> questions = questionLoaderService.loadQuestionStrings();
        logger.info("Loaded {} questions for run {}", questions.size(), run.getId());
        
        // Update run statistics
        run.setTotalRecords(questions.size());
        run.setCompletedRecords(0);
        run.setStatus(RunStatus.CREATED);
        
        // Save the run first
        EvaluationRun savedRun = evaluationDataService.saveRun(run);
        
        // Create evaluation records for each question
        List<EvaluationRecord> records = new ArrayList<>();
        for (String question : questions) {
            EvaluationRecord record = createEvaluationRecord(question, savedRun.getId());
            records.add(record);
        }
        
        // Save all records
        for (EvaluationRecord record : records) {
            evaluationDataService.saveRecord(record);
        }
        
        logger.info("Created {} evaluation records for run {}", records.size(), savedRun.getId());
        return savedRun;
    }
    
    /**
     * Executes an evaluation run by generating agent responses for all questions
     * This method runs synchronously and may take time depending on the number of questions
     * 
     * @param runId The ID of the run to execute
     * @return The updated run with completion status
     */
    public EvaluationRun executeRun(String runId) {
        logger.info("Executing evaluation run: {}", runId);
        
        EvaluationRun run = evaluationDataService.getRunById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
        
        // Update run status
        run.setStatus(RunStatus.RUNNING);
        evaluationDataService.saveRun(run);
        
        try {
            // Get all records for this run
            List<EvaluationRecord> records = evaluationDataService.getRecordsByRunId(runId);
            logger.info("Found {} records to process for run {}", records.size(), runId);
            
            int completed = 0;
            for (EvaluationRecord record : records) {
                // Skip if already has a response
                if (record.getResponse() == null || record.getResponse().isEmpty()) {
                    try {
                        // Generate response using AgentRunner
                        long startTime = System.currentTimeMillis();
                        String response = agentRunner.generateResponse(record.getInput());
                        long processingTime = System.currentTimeMillis() - startTime;
                        
                        // Update record with response and metadata
                        record.setResponse(response);
                        
                        // For demo purposes, generate a random LLM score
                        generateDummyLlmScore(record);
                        
                        // Update metadata if available
                        if (record.getMetadata() != null) {
                            record.getMetadata().setProcessingTimeMs(processingTime);
                        }
                        
                        // Save updated record
                        evaluationDataService.saveRecord(record);
                        completed++;
                        
                        logger.debug("Processed question {}/{} for run {}", completed, records.size(), runId);
                        
                    } catch (Exception e) {
                        logger.error("Failed to process record {} in run {}", record.getId(), runId, e);
                        // Continue with other records
                    }
                } else {
                    completed++;
                    logger.debug("Skipping record {} - already has response", record.getId());
                }
                
                // Update run progress
                run.setCompletedRecords(completed);
                evaluationDataService.saveRun(run);
            }
            
            // Mark run as completed
            run.setStatus(RunStatus.COMPLETED);
            run.setCompletedAt(LocalDateTime.now());
            
            logger.info("Completed evaluation run {} with {}/{} records processed", 
                       runId, completed, records.size());
            
        } catch (Exception e) {
            logger.error("Failed to execute run {}", runId, e);
            run.setStatus(RunStatus.FAILED);
        }
        
        return evaluationDataService.saveRun(run);
    }
    
    /**
     * Executes an evaluation run asynchronously
     * 
     * @param runId The ID of the run to execute
     * @return CompletableFuture that completes when the run is finished
     */
    public CompletableFuture<EvaluationRun> executeRunAsync(String runId) {
        return CompletableFuture.supplyAsync(() -> executeRun(runId));
    }
    
    /**
     * Creates a single evaluation record from a question
     * 
     * @param question The input question
     * @param runId The ID of the evaluation run
     * @return A new EvaluationRecord
     */
    private EvaluationRecord createEvaluationRecord(String question, String runId) {
        EvaluationRecord record = new EvaluationRecord();
        record.setId(UUID.randomUUID().toString());
        record.setRunId(runId);
        record.setInput(question);
        // Response will be generated later during execution
        record.setResponse("");
        
        // Initialize with unknown scores
        record.setLlmScore(ScoreType.UNKNOWN);
        record.setLlmReason("");
        record.setHumanScore(ScoreType.UNKNOWN);
        record.setHumanReason("");
        
        record.setTimestamp(LocalDateTime.now());
        
        return record;
    }
    
    /**
     * Generates a dummy LLM score for demonstration purposes
     * In a real implementation, this would be done by an actual evaluation model
     */
    private void generateDummyLlmScore(EvaluationRecord record) {
        // Simple heuristic based on response length and content
        String response = record.getResponse().toLowerCase();
        
        ScoreType score;
        String reason;
        
        if (response.length() < 50) {
            score = ScoreType.BAD;
            reason = "Response is too short and lacks detail";
        } else if (response.contains("error") || response.contains("sorry") || response.contains("cannot")) {
            score = ScoreType.BAD;
            reason = "Response indicates inability to answer the question properly";
        } else if (response.length() > 100 && (response.contains("analysis") || response.contains("information"))) {
            score = ScoreType.GOOD;
            reason = "Response provides detailed analysis and relevant information";
        } else {
            score = ScoreType.GOOD;
            reason = "Response appears to address the question adequately";
        }
        
        record.setLlmScore(score);
        record.setLlmReason(reason);
    }
    
    /**
     * Gets the number of available questions from the input file
     * 
     * @return Number of questions available
     */
    public int getAvailableQuestionCount() {
        return questionLoaderService.getQuestionCount();
    }
    
    /**
     * Checks if the question loader and agent runner are ready
     * 
     * @return true if both services are ready
     */
    public boolean isReady() {
        boolean questionLoaderReady = questionLoaderService.isQuestionsFileAvailable();
        boolean agentRunnerReady = agentRunner.isReady();
        
        logger.debug("Service readiness - Questions: {}, AgentRunner: {}", 
                    questionLoaderReady, agentRunnerReady);
        
        return questionLoaderReady && agentRunnerReady;
    }
    
    /**
     * Gets status information about the evaluation runner service
     * 
     * @return Status string with service information
     */
    public String getServiceStatus() {
        return String.format("EvaluationRunnerService - Questions: %s, Agent: %s, Available Questions: %d",
                           questionLoaderService.isQuestionsFileAvailable() ? "Ready" : "Not Ready",
                           agentRunner.isReady() ? "Ready" : "Not Ready",
                           getAvailableQuestionCount());
    }
}
