package com.doctortsab.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Configuration class for AI Service API
 */
@Configuration
@Component
public class AIServiceConfig {

    @Value("${ai.api.key}")
    private String apiKey;

    /**
     * Returns the AI service API key
     * @return the API key
     */
    public String getApiKey() {
        return apiKey;
    }
}
