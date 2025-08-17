package org.rag4j.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.rag4j.webapp", "org.rag4j.agent"})
public class ThymeleafAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThymeleafAgentApplication.class, args);
    }

}
