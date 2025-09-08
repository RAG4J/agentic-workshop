package org.rag4j.agent.springai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.rag4j.agent.springai.config.ObservabilityProperties;
import org.rag4j.agent.springai.model.ChatInteractionLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ChatLoggingService {

    private static final Logger logger = LoggerFactory.getLogger(ChatLoggingService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final ObservabilityProperties properties;
    private final ObjectMapper objectMapper;
    private final AtomicLong fileCounter = new AtomicLong(1);

    public ChatLoggingService(ObservabilityProperties properties) {
        logger.info("Initializing ChatLoggingService with properties: {}", properties);
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void logInteraction(ChatInteractionLog interactionLog) {
        if (!properties.isEnabled()) {
            logger.warn("Observability logging is disabled, skipping interaction log");
            return;
        }

        try {
            Path logFile = createLogFile();
            objectMapper.writeValue(logFile.toFile(), interactionLog);
            logger.debug("Successfully logged interaction to: {}", logFile);
        } catch (IOException e) {
            logger.error("Failed to log chat interaction", e);
        }
    }

    private Path createLogFile() throws IOException {
        Path baseDir = properties.getLogDirectoryPath();
        if (!baseDir.isAbsolute()) {
            // If relative path, make it relative to current working directory
            baseDir = Path.of(System.getProperty("user.dir")).resolve(baseDir);
        }

        String today = LocalDate.now().format(DATE_FORMATTER);
        Path todayDir = baseDir.resolve(today);
        
        // Ensure directory exists
        Files.createDirectories(todayDir);

        // Find next available file number
        long currentCounter;
        Path logFile;
        do {
            currentCounter = fileCounter.getAndIncrement();
            logFile = todayDir.resolve(currentCounter + ".json");
        } while (Files.exists(logFile));

        return logFile;
    }

    public void resetDailyCounter() {
        // This method can be called by a scheduled task to reset counter at midnight
        // For now, we'll let the counter continue incrementally
        fileCounter.set(1);
    }
}
