package org.rag4j.agent.springai.bedrock;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

public class ProxyBedrockRuntimeClient implements BedrockRuntimeClient {

    @Override
    public String serviceName() {
        return "";
    }

    @Override
    public void close() {

    }
}
