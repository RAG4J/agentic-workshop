package org.rag4j.webapp;

import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservabilityDebugConfig {

    private static final Logger logger = LoggerFactory.getLogger(ObservabilityDebugConfig.class);

    @Bean
    public CommandLineRunner debugObservability(@Autowired ObservationRegistry observationRegistry) {
        return args -> {
            logger.info("=== ObservabilityRegistry Debug Info ===");
            logger.info("ObservationRegistry type: {}", observationRegistry.getClass().getName());
            logger.info("Is NOOP registry: {}", observationRegistry.getClass().getName().contains("Noop"));
            
            // Try to get current configuration
            try {
                logger.info("ObservationRegistry toString: {}", observationRegistry.toString());
            } catch (Exception e) {
                logger.warn("Could not get registry info", e);
            }
            
            // Check if observationHandlers are configured
            try {
                // This is a simple way to check if the registry is properly configured
                var observation = observationRegistry.getCurrentObservationScope();
                if (observation != null) {
                    logger.info("Current observation scope: {}", observation);
                } else {
                    logger.info("No current observation scope");
                }
            } catch (Exception e) {
                logger.info("Exception checking observation scope (this might be normal): {}", e.getMessage());
            }
            
            logger.info("=== End ObservabilityRegistry Debug Info ===");
        };
    }
}
