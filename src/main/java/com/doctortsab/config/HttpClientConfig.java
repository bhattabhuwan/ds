package com.doctortsab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for HTTP client components
 */
@Configuration
public class HttpClientConfig {

    /**
     * Creates and configures the RestTemplate bean with longer timeout values
     * and better error handling for AI API requests
     * @return RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Set longer timeout for AI API calls (15 seconds connection, 60 seconds for read)
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(60000);
        
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }
}
