package org.rag4j.webapp;

import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"org.rag4j.webapp", "org.rag4j.agent"})
public class ThymeleafAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThymeleafAgentApplication.class, args);
    }

    @Bean
    SpanExporter loggingSpanExporter() {
        // Writes human-readable span lines to your app logs
        return LoggingSpanExporter.create();
    }
}
