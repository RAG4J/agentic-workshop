package org.rag4j.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("plain")
@Import(PlainProfileTestConfig.class)
class ThymeleafAgentApplicationTests {

    @Test
    void contextLoads() {
        // The goal is to load the context without any issues.
    }

}
