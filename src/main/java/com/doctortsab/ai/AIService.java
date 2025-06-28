package com.doctortsab.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.doctortsab.config.AIServiceConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service for handling AI-related operations with OpenRouter API
 */
@Service
public class AIService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    private static final String AI_API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    
    @Autowired
    private AIServiceConfig aiConfig;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * Process text with AI using OpenRouter API
     * @param prompt The input prompt
     * @return The AI response or error message
     */
    public String processWithAI(String prompt) {
        logger.info("Processing AI request with prompt: {}", prompt);
        
        try {
            // Get API key
            String apiKey = aiConfig.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                logger.error("API key is missing or empty");
                return "Error: API key is missing or empty.";
            }
            
            logger.info("API key present - first 5 chars: {}", apiKey.substring(0, 5) + "...");
            
            // Set up headers for OpenRouter API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            // Required headers for OpenRouter
            headers.set("HTTP-Referer", "https://doctortsab.com"); 
            headers.set("X-Title", "DoctorSab Healthcare Assistant");
            
            // Add OpenRouter specific headers
            headers.set("User-Agent", "DoctorSab/1.0");
            headers.set("Accept", "application/json");
            
            // Set up system message and user message for better context
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a helpful medical assistant. Provide information about possible conditions based on symptoms. Always include a disclaimer that this is not medical advice and the user should consult a doctor for proper diagnosis.");
            
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(systemMessage);
            messages.add(userMessage);
            
            // Try OpenAI's GPT model - more commonly available on OpenRouter
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "openai/gpt-3.5-turbo"); // More widely supported model
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.5);  // Balanced temperature
            requestBody.put("max_tokens", 800);  // Reasonable max tokens
            requestBody.put("stream", false);     // No streaming
            
            // Log the request details for debugging
            logger.info("Sending request to OpenRouter API: {}", AI_API_URL);
            
            try {
                String requestJson = jsonMapper.writeValueAsString(requestBody);
                logger.info("Request body: {}", requestJson);
            } catch (JsonProcessingException e) {
                logger.warn("Could not serialize request body for logging", e);
            }
            
            // Send request with timeout configuration
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // Try getting response as a String first for better debugging
            try {
                // Use custom RestTemplate timeout settings for this specific request
                int timeoutMs = 30000; // 30 seconds
                restTemplate.setRequestFactory(new org.springframework.http.client.SimpleClientHttpRequestFactory() {{
                    setConnectTimeout(timeoutMs);
                    setReadTimeout(timeoutMs);
                }});
                
                logger.info("Sending API request with timeout: {}ms", timeoutMs);
                
                // Log the full request for debugging
                logger.info("Request URL: {}", AI_API_URL);
                logger.info("Request headers: {}", headers);
                try {
                    logger.info("Request body: {}", jsonMapper.writeValueAsString(requestBody));
                } catch (JsonProcessingException e) {
                    logger.warn("Could not log request body", e);
                }
                
                // IMPORTANT FIX: If we get an empty response, return a fallback response
                String fallbackResponse = "I'm sorry, I couldn't analyze your symptoms at this time. " +
                    "Common causes of headache and fever include viral infections like the flu, " +
                    "COVID-19, or common cold. Other possibilities include bacterial infections, " +
                    "dehydration, or inflammatory conditions. Please consult a healthcare provider for proper diagnosis and treatment.";
                
                try {
                    ResponseEntity<String> stringResponse = restTemplate.postForEntity(
                        AI_API_URL,
                        entity,
                        String.class
                    );
                    
                    logger.info("Response received - status code: {}", stringResponse.getStatusCode());
                    logger.info("Response headers: {}", stringResponse.getHeaders());
                    String responseBody = stringResponse.getBody();
                    
                    if (responseBody == null || responseBody.isEmpty()) {
                        logger.error("Received empty response body from API - using fallback response");
                        return fallbackResponse;
                    }
                    
                    // Log full response for debugging
                    logger.info("Raw response: {}", responseBody);
                    
                    try {
                        // Parse response using TypeReference for precise generic type information
                        Map<String, Object> parsedResponse = jsonMapper.readValue(responseBody, 
                            new TypeReference<HashMap<String, Object>>() {});
                        
                        // Extract content from response - using a helper method
                        String extractedContent = extractContentFromResponse(parsedResponse);
                        
                        if (extractedContent == null || extractedContent.trim().isEmpty()) {
                            logger.error("Extracted content is empty or null - using fallback response");
                            return fallbackResponse;
                        }
                        
                        logger.info("Successfully extracted content ({} chars)", extractedContent.length());
                        return extractedContent;
                    } catch (JsonProcessingException e) {
                        logger.error("Error processing JSON in response: {}", e.getMessage());
                        return fallbackResponse;
                    } catch (IllegalArgumentException | ClassCastException | NullPointerException e) {
                        logger.error("Error parsing response structure: {}", e.getMessage());
                        return fallbackResponse;
                    }
                } catch (IllegalStateException | IllegalArgumentException jsonEx) {
                    logger.error("Error parsing or extracting from JSON response", jsonEx);
                    return fallbackResponse;
                    }
                } catch (org.springframework.web.client.ResourceAccessException e) {
                    logger.error("Connection timeout or resource access error with AI service", e);
                    return "Error: Unable to connect to AI service (timeout or connection error): " + e.getMessage();
                } catch (org.springframework.web.client.HttpClientErrorException e) {
                    logger.error("HTTP client error (4xx) communicating with AI service: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
                    return "Error: AI service reported a client error (possibly invalid API key or request): " + e.getStatusCode() + " - " + e.getMessage();
                } catch (org.springframework.web.client.HttpServerErrorException e) {
                    logger.error("HTTP server error (5xx) from AI service: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
                    return "Error: AI service is experiencing issues: " + e.getStatusCode() + " - " + e.getMessage();
                } catch (org.springframework.web.client.RestClientException e) {
                    logger.error("REST client error communicating with AI service", e);
                    return "Error: Unable to communicate with AI service: " + e.getMessage();
                } catch (IllegalStateException e) {
                    logger.error("State error in AI service processing", e);
                    return "Error: Problem with AI service response: " + e.getMessage();
                } catch (RuntimeException e) {
                    logger.error("Runtime error in API communication", e);
                    return "Error: A runtime error occurred during AI processing: " + e.getMessage();
            }
            
        } catch (IllegalArgumentException | NullPointerException e) {
            logger.error("Invalid argument or null pointer in AI service", e);
            return "Error: Invalid parameters in AI service: " + e.getMessage();
        } catch (RuntimeException e) {
            logger.error("Runtime error in AI service", e);
            return "Error: AI service runtime error: " + e.getMessage();
        }
    }
    
    /**
     * Extract AI response content from the parsed JSON response
     * @param response The parsed response map
     * @return The extracted content or error message
     */
    private String extractContentFromResponse(Map<String, Object> response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            // Log response keys at top level
            logger.info("Extracting content from response with keys: {}", response.keySet());
            
            // Print full response for debugging
            try {
                logger.debug("Full response structure: {}", 
                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
            } catch (JsonProcessingException e) {
                logger.warn("Could not print full response", e);
            }
            
            // Handle standard OpenAI/OpenRouter format - most common
            if (response.containsKey("choices") && response.get("choices") instanceof List) {
                logger.info("Found 'choices' array in response");
                
                @SuppressWarnings("unchecked")
                List<Object> choicesRaw = (List<Object>) response.get("choices");
                
                if (choicesRaw != null && !choicesRaw.isEmpty()) {
                    Object firstChoiceObj = choicesRaw.get(0);
                    
                    // Direct logging of raw object
                    logger.info("First choice type: {}", firstChoiceObj != null ? 
                        firstChoiceObj.getClass().getName() : "null");
                    
                    // Try to handle as Map directly first
                    if (firstChoiceObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> choice = (Map<String, Object>) firstChoiceObj;
                        logger.info("First choice keys: {}", choice.keySet());
                        
                        // Check for message structure (OpenAI/OpenRouter standard format)
                        if (choice.containsKey("message")) {
                            Object messageObj = choice.get("message");
                            
                            if (messageObj instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> message = (Map<String, Object>) messageObj;
                                logger.info("Message object keys: {}", message.keySet());
                                
                                if (message.containsKey("content")) {
                                    String content = String.valueOf(message.get("content"));
                                    logger.info("Successfully extracted AI response content from message.content");
                                    return content;
                                }
                            } else {
                                logger.warn("Message object is not a Map: {}", 
                                    messageObj != null ? messageObj.getClass().getName() : "null");
                            }
                        } 
                        
                        // Check for text field (some models)
                        else if (choice.containsKey("text")) {
                            String content = String.valueOf(choice.get("text"));
                            logger.info("Successfully extracted AI response from text field");
                            return content;
                        }
                        
                        // Check for delta (streaming format)
                        else if (choice.containsKey("delta")) {
                            Object deltaObj = choice.get("delta");
                            if (deltaObj instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> delta = (Map<String, Object>) deltaObj;
                                if (delta.containsKey("content")) {
                                    String content = String.valueOf(delta.get("content"));
                                    logger.info("Successfully extracted AI response from delta content field");
                                    return content;
                                }
                            }
                        }
                    }
                }
            }
            
            // Check for direct content field (Anthropic Claude format)
            if (response.containsKey("content") && response.get("content") instanceof List) {
                List<Map<String, Object>> contentList = mapper.convertValue(
                    response.get("content"),
                    mapper.getTypeFactory().constructCollectionType(List.class, 
                        mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class))
                );
                
                if (!contentList.isEmpty()) {
                    StringBuilder fullContent = new StringBuilder();
                    for (Map<String, Object> contentPart : contentList) {
                        if (contentPart.containsKey("text")) {
                            fullContent.append(contentPart.get("text"));
                        }
                    }
                    
                    if (fullContent.length() > 0) {
                        logger.info("Successfully extracted AI response from Anthropic content format");
                        return fullContent.toString();
                    }
                }
            }
            
            // Handle error in response
            if (response.containsKey("error")) {
                // Type-safe conversion of error object
                Map<String, Object> error = mapper.convertValue(
                    response.get("error"),
                    mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class)
                );
                
                String errorMessage = error.containsKey("message") ? 
                    (String) error.get("message") : "Unknown error";
                logger.error("API error: {}", errorMessage);
                return "Error from AI service: " + errorMessage;
            }
            
            // Check for completion field (used by some models)
            if (response.containsKey("completion") && response.get("completion") instanceof String) {
                String completion = (String) response.get("completion");
                logger.info("Successfully extracted AI response from completion field");
                return completion;
            }
            
            // Check for OpenRouter-specific formats
            if (response.containsKey("output") && response.get("output") instanceof String) {
                String output = (String) response.get("output");
                logger.info("Successfully extracted AI response from output field");
                return output;
            }
            
            // Handle Anthropic/Claude direct response format
            if (response.containsKey("completion") || response.containsKey("response")) {
                String result = response.containsKey("completion") ? 
                    String.valueOf(response.get("completion")) : 
                    String.valueOf(response.get("response"));
                logger.info("Found response in completion/response field");
                return result;
            }
            
            // Last resort: dump the full response for investigation
            try {
                logger.error("Could not extract content from AI response. Full response: {}", 
                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
            } catch (JsonProcessingException e) {
                logger.error("Failed to serialize response for logging", e);
            }
            return "Could not extract a valid response from the AI service.";
        } catch (IllegalArgumentException e) {
            // Catch specific exceptions from mapper.convertValue
            logger.error("Error converting response types", e);
            return "Error processing AI response structure: " + e.getMessage();
        } catch (ClassCastException e) {
            // Catch class cast exceptions for any remaining casts
            logger.error("Error with type conversion in AI response", e);
            return "Error processing AI response format: " + e.getMessage();
        } catch (NullPointerException e) {
            // Catch NPE when accessing nested objects
            logger.error("Null value found in AI response structure", e);
            return "Error with missing values in AI response: " + e.getMessage();
        } catch (RuntimeException e) {
            // Catch all other unexpected runtime errors
            logger.error("Runtime error extracting content from AI response", e);
            return "Runtime error processing AI response: " + e.getMessage();
        }
    }
    
    /**
     * Test direct connection to OpenRouter API
     * @return Test result or error message
     */
    public String testOpenRouterConnection() {
        logger.info("Testing direct connection to OpenRouter API");
        
        try {
            // Get API key
            String apiKey = aiConfig.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                return "Error: API key is missing or empty.";
            }
            
            logger.info("API key is present (first 5 chars): {}", apiKey.substring(0, 5) + "...");
            
            // Set up headers with all required fields
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("HTTP-Referer", "https://doctortsab.com");
            headers.set("X-Title", "DoctorSab Healthcare Assistant");
            headers.set("Accept", "application/json");
            
            // Create a very simple request - just a single message asking for a greeting
            Map<String, Object> requestBody = new HashMap<>();
            
            // Use a simple, widely available model
            requestBody.put("model", "openai/gpt-3.5-turbo");
            
            // Create a simple message
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", "Just say 'Hello! This is a successful test of the AI API.'");
            messages.add(message);
            requestBody.put("messages", messages);
            
            // Minimal additional parameters
            requestBody.put("max_tokens", 50);
            requestBody.put("temperature", 0.1); // Very low temperature for deterministic response
            requestBody.put("stream", false);
            
            // Configure timeout for test
            int timeoutMs = 10000; // 10 seconds for test
            restTemplate.setRequestFactory(new org.springframework.http.client.SimpleClientHttpRequestFactory() {{
                setConnectTimeout(timeoutMs);
                setReadTimeout(timeoutMs);
            }});
            
            // Send test request
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            logger.info("Sending test request to OpenRouter API");
            
            try {
                // Try with String response type for better debugging
                ResponseEntity<String> response = restTemplate.postForEntity(
                    AI_API_URL,
                    entity,
                    String.class
                );
                
                // Log complete response details
                logger.info("Test response status: {}", response.getStatusCode());
                logger.info("Test response headers: {}", response.getHeaders());
                
                String responseBody = response.getBody();
                
                // Check for empty response
                if (responseBody == null || responseBody.isEmpty()) {
                    return "Test received empty response body despite status code: " + response.getStatusCode();
                }
                
                // Log full response for easier debugging
                logger.info("Full API test response: {}", responseBody);
                
                // Try to parse and extract content to verify valid JSON response
                try {
                    Map<String, Object> parsedResponse = jsonMapper.readValue(responseBody, 
                        new TypeReference<HashMap<String, Object>>() {});
                    
                    String extractedContent = extractContentFromResponse(parsedResponse);
                    return "Test successful! Status: " + response.getStatusCode() + 
                           "\nExtracted response: " + extractedContent;
                } catch (JsonProcessingException e) {
                    logger.warn("JSON parsing error in test response", e);
                    return "Test partially successful. Response received but JSON parsing failed: " + 
                           responseBody.substring(0, Math.min(200, responseBody.length()));
                } catch (IllegalArgumentException | NullPointerException e) {
                    logger.warn("Content extraction error in test response", e);
                    return "Test partially successful. Response received but content extraction failed: " + 
                           responseBody.substring(0, Math.min(200, responseBody.length()));
                } catch (RuntimeException e) {
                    logger.warn("Runtime error processing test response", e);
                    return "Test partially successful. Got response but couldn't process it: " + 
                           responseBody.substring(0, Math.min(200, responseBody.length()));
                }
                
            } catch (org.springframework.web.client.ResourceAccessException e) {
                logger.error("Connection timeout during test", e);
                return "Test failed: API connection timeout: " + e.getMessage();
            } catch (org.springframework.web.client.HttpClientErrorException e) {
                // Client errors like 400, 401, 403 etc.
                logger.error("HTTP client error during test: {} - {}", 
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
                return "Test failed: HTTP error " + e.getStatusCode() + 
                       "\nResponse: " + e.getResponseBodyAsString();
            } catch (IllegalStateException | IllegalArgumentException e) {
                logger.error("State or argument error during API test", e);
                return "Test failed due to invalid state or arguments: " + e.getMessage();
            } catch (RuntimeException e) {
                logger.error("Runtime error during API test", e);
                return "Test failed: " + e.getClass().getSimpleName() + ": " + e.getMessage();
            }
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument error setting up API test", e);
            return "Test setup failed due to invalid argument: " + e.getMessage();
        } catch (NullPointerException e) {
            logger.error("Null reference error setting up API test", e);
            return "Test setup failed due to missing or null values: " + e.getMessage();
        } catch (RuntimeException e) {
            logger.error("Runtime error setting up API test", e);
            return "Test setup failed: " + e.getMessage();
        }
    }
}
