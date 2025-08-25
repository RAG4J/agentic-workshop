package org.rag4j.agent.springai.bedrock;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "proxy.bedrock")
public record ProxyBedrockProperties(String baseUrl, String apiKey, String modelId) {
}
