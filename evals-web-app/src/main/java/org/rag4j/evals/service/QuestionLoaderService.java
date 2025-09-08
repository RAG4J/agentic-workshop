package org.rag4j.evals.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rag4j.evals.model.InputQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.io.FileInputStream;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for loading questions from the input JSON file
 */
@Service
public class QuestionLoaderService {
    
    private static final Logger logger = LoggerFactory.getLogger(QuestionLoaderService.class);
    
    private final ObjectMapper objectMapper;
    private final String questionsFilePath;
    
    public QuestionLoaderService(ObjectMapper objectMapper, 
                                @Value("${evals.data.directory:evals-web-app/src/main/data}") String dataDirectory,
                                @Value("${evals.questions.filename:input-questions.json}") String questionsFileName) {
        this.objectMapper = objectMapper;
        // Use the same base directory as other data files
        this.questionsFilePath = dataDirectory + "/" + questionsFileName;
    }
    
    /**
     * Loads questions from the input JSON file
     * 
     * @return List of InputQuestion objects
     * @throws IOException if the file cannot be read
     */
    public List<InputQuestion> loadQuestions() throws IOException {
        logger.info("Loading questions from file: {}", questionsFilePath);
        
        try {
            File questionsFile = new File(questionsFilePath);
            
            if (!questionsFile.exists()) {
                logger.warn("Questions file not found: {}. Creating default questions.", questionsFilePath);
                return createDefaultQuestions();
            }
            
            try (InputStream inputStream = new FileInputStream(questionsFile)) {
                List<InputQuestion> questions = objectMapper.readValue(inputStream, 
                        new TypeReference<List<InputQuestion>>() {});
                
                logger.info("Successfully loaded {} questions from file", questions.size());
                return questions;
            }
            
        } catch (IOException e) {
            logger.error("Failed to load questions from file: {}", questionsFilePath, e);
            throw new IOException("Failed to load questions from file: " + questionsFilePath, e);
        }
    }
    
    /**
     * Gets just the question strings from the input file
     * 
     * @return List of question strings
     * @throws IOException if the file cannot be read
     */
    public List<String> loadQuestionStrings() throws IOException {
        List<InputQuestion> questions = loadQuestions();
        return questions.stream()
                .map(InputQuestion::getQuestion)
                .toList();
    }
    
    /**
     * Counts the number of questions in the input file
     * 
     * @return Number of questions
     */
    public int getQuestionCount() {
        try {
            return loadQuestions().size();
        } catch (IOException e) {
            logger.error("Failed to count questions", e);
            return 0;
        }
    }
    
    /**
     * Checks if the questions file exists and is readable
     * 
     * @return true if the file exists and is readable
     */
    public boolean isQuestionsFileAvailable() {
        try {
            File questionsFile = new File(questionsFilePath);
            return questionsFile.exists() && questionsFile.canRead();
        } catch (Exception e) {
            logger.warn("Error checking questions file availability", e);
            return false;
        }
    }
    
    /**
     * Creates default questions if the input file is not found
     * 
     * @return List of default InputQuestion objects
     */
    private List<InputQuestion> createDefaultQuestions() {
        logger.info("Creating default questions");
        
        List<InputQuestion> defaultQuestions = new ArrayList<>();
        defaultQuestions.add(new InputQuestion("What is artificial intelligence?"));
        defaultQuestions.add(new InputQuestion("How do machine learning algorithms work?"));
        defaultQuestions.add(new InputQuestion("What are the benefits of using AI in business?"));
        defaultQuestions.add(new InputQuestion("What is the difference between AI and machine learning?"));
        defaultQuestions.add(new InputQuestion("How can AI help improve software development?"));
        
        logger.info("Created {} default questions", defaultQuestions.size());
        return defaultQuestions;
    }
    
    /**
     * Gets information about the questions file
     * 
     * @return String with file information
     */
    public String getFileInfo() {
        try {
            File questionsFile = new File(questionsFilePath);
            if (questionsFile.exists()) {
                int count = getQuestionCount();
                return String.format("File: %s, Questions: %d, Available: %s", 
                                    questionsFilePath, count, questionsFile.canRead());
            } else {
                return String.format("File: %s, Status: Not found", questionsFilePath);
            }
        } catch (Exception e) {
            return String.format("File: %s, Status: Error - %s", questionsFilePath, e.getMessage());
        }
    }
}
