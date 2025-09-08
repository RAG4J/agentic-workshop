package org.rag4j.evals;

import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EvalsWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvalsWebApplication.class, args);
    }

    @Bean
    SpanExporter loggingSpanExporter() {
        // Writes human-readable span lines to your app logs
        return LoggingSpanExporter.create();
    }
}
