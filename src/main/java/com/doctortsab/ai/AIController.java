package com.doctortsab.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for AI-related endpoints
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {
    
    private static final Logger logger = LoggerFactory.getLogger(AIController.class);
    
    @Autowired
    private AIService aiService;
    
    /**
     * Endpoint to process text with AI
     * @param request The request containing the prompt
     * @return The AI response
     */
    @PostMapping("/process")
    public ResponseEntity<AIResponse> processText(@RequestBody AIRequest request) {
        try {
            if (request == null || request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                logger.warn("Empty prompt received");
                return ResponseEntity.ok(new AIResponse("Please provide a prompt."));
            }
            
            logger.info("Processing general AI request: {}", request.getPrompt());
            String result = aiService.processWithAI(request.getPrompt());
            
            // Validate response is not empty
            if (result == null || result.trim().isEmpty()) {
                logger.warn("Empty AI response received");
                result = "I apologize, but I couldn't generate a response. Please try again.";
            }
            
            return ResponseEntity.ok(new AIResponse(result));
        } catch (Exception e) {
            logger.error("Error in process endpoint", e);
            return ResponseEntity.ok(new AIResponse("An error occurred: " + e.getMessage()));
        }
    }
    
    /**
     * Specialized endpoint for first aid guidance
     * @param request The request containing the first aid situation
     * @return The AI response with first aid guidance
     */
    @PostMapping("/first-aid")
    public ResponseEntity<AIResponse> processFirstAid(@RequestBody AIRequest request) {
        try {
            if (request == null || request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                logger.warn("Empty first aid prompt received");
                return ResponseEntity.ok(new AIResponse("Please describe the first aid situation."));
            }
            
            logger.info("Processing first aid request: {}", request.getPrompt());
            String enhancedPrompt = "Provide detailed first aid instructions for: " + request.getPrompt();
            String result = aiService.processWithAI(enhancedPrompt);
            
            // Ensure we never return null or empty response
            if (result == null || result.trim().isEmpty()) {
                logger.warn("Empty first aid AI response received");
                result = "I apologize, but I'm having trouble providing first aid guidance at the moment. Please try again later.";
            }
            
            return ResponseEntity.ok(new AIResponse(result));
        } catch (Exception e) {
            logger.error("Error in first aid endpoint", e);
            // Always return a valid response even on error
            return ResponseEntity.ok(new AIResponse("An error occurred while processing your request: " + e.getMessage()));
        }
    }
      /**
     * Specialized endpoint for symptom checking
     * @param request The request containing symptoms
     * @return The AI response with possible conditions and recommendations
     */    @PostMapping("/symptom-check")
    public ResponseEntity<AIResponse> processSymptomCheck(@RequestBody AIRequest request) {
        try {
            if (request == null || request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                logger.warn("Empty symptom check prompt received");
                return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(new AIResponse("Please provide some symptoms to analyze."));
            }
            
            logger.info("Processing symptom check request: {}", request.getPrompt());
            
            // Generate a fallback response based on the symptoms
            String symptoms = request.getPrompt().toLowerCase();
            String fallbackResponse = generateFallbackResponse(symptoms);
            
            try {
                // Try AI service first
                // Add a more specific medical context prompt for symptom checking
                String enhancedPrompt = "You are a medical assistant providing a preliminary assessment. " +
                    "Analyze these symptoms and provide possible conditions and recommendations: " + request.getPrompt() +
                    " Format your answer in clear sections including 'Possible Conditions', 'Recommendations', and 'Important Note'.";
                
                // Log the enhanced prompt for debugging
                logger.info("Enhanced prompt for symptom check: {}", enhancedPrompt);
                
                String result = aiService.processWithAI(enhancedPrompt);
                
                // Ensure we never return null or empty response
                if (result == null || result.trim().isEmpty()) {
                    logger.warn("Empty symptom check AI response received - using fallback");
                    result = fallbackResponse;
                }
                      logger.info("Successfully generated response ({} chars)", result.length());
                
                // Explicitly set the content type to application/json
                return ResponseEntity.ok()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(new AIResponse(result));
                    
            } catch (Exception aiEx) {
                // If AI service fails, use our fallback response
                logger.error("AI service error - using fallback response", aiEx);
                  return ResponseEntity.ok()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(new AIResponse(fallbackResponse));
            }
        } catch (Exception e) {
            logger.error("Error in symptom check endpoint", e);
            // Always return a valid response even on error
            return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(new AIResponse("An error occurred while processing your request: " + e.getMessage()));
        }
    }
    
    /**
     * Generate a fallback response based on common symptoms
     * @param symptoms The user's symptoms in lowercase
     * @return A formatted response
     */
    private String generateFallbackResponse(String symptoms) {
        StringBuilder response = new StringBuilder();
        response.append("## Possible Conditions\n\n");
        
        // Check for common symptom patterns
        if (symptoms.contains("headache") || symptoms.contains("head pain") || symptoms.contains("migraine")) {
            if (symptoms.contains("fever") || symptoms.contains("temperature")) {
                response.append("- **Viral Infection**: Headache with fever is commonly seen in viral infections like influenza or COVID-19\n");
                response.append("- **Sinusitis**: Inflammation of the sinuses can cause headaches and sometimes fever\n");
                response.append("- **Meningitis**: Though less common, inflammation of the membranes covering the brain can cause severe headache and fever\n\n");
            } else {
                response.append("- **Tension Headache**: Often related to stress, anxiety, or muscle tension\n");
                response.append("- **Migraine**: Recurrent headaches that can be moderate to severe\n");
                response.append("- **Dehydration**: Lack of fluids can trigger headaches\n\n");
            }
        } else if (symptoms.contains("cough")) {
            if (symptoms.contains("fever") || symptoms.contains("temperature")) {
                response.append("- **Bronchitis**: Inflammation of the bronchial tubes\n");
                response.append("- **Pneumonia**: Infection affecting the air sacs in one or both lungs\n");
                response.append("- **COVID-19**: Viral respiratory illness that can present with fever and cough\n\n");
            } else {
                response.append("- **Common Cold**: Viral infection of the upper respiratory tract\n");
                response.append("- **Allergies**: Exposure to allergens can trigger coughing\n");
                response.append("- **Asthma**: Chronic condition affecting the airways\n\n");
            }
        } else if (symptoms.contains("stomach") || symptoms.contains("nausea") || symptoms.contains("vomit")) {
            response.append("- **Gastroenteritis**: Inflammation of the stomach and intestines\n");
            response.append("- **Food Poisoning**: Consuming contaminated food or beverages\n");
            response.append("- **Peptic Ulcer**: Sore in the lining of the stomach or duodenum\n\n");
        } else {
            // Generic response for other symptoms
            response.append("- **Viral Infection**: Many common symptoms are caused by viral infections\n");
            response.append("- **Bacterial Infection**: Some symptoms may indicate bacterial infections\n");
            response.append("- **Inflammatory Conditions**: Inflammation can cause various symptoms\n\n");
        }
        
        response.append("## Recommendations\n\n");
        response.append("- Rest and ensure adequate hydration\n");
        response.append("- Monitor your symptoms and their progression\n");
        response.append("- Consider over-the-counter medications appropriate for your symptoms\n");
        response.append("- Seek medical attention if symptoms worsen or persist\n\n");
        
        response.append("## Important Note\n\n");
        response.append("This is not a medical diagnosis. The information provided is for general guidance only. Always consult with a healthcare professional for proper diagnosis and treatment.");
        
        return response.toString();
    }
      /**
     * Test endpoint to verify basic connectivity
     * @return Simple success message
     */    @GetMapping("/test-basic")
    public ResponseEntity<String> testEndpoint() {
        logger.info("Testing basic API connectivity");
        return ResponseEntity.ok()
            .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
            .body("API connectivity test successful. Timestamp: " + System.currentTimeMillis());
    }
      /**
     * Test endpoint for verifying the AI API connection
     * @return A test response from the AI
     */
    @GetMapping("/test")
    public ResponseEntity<AIResponse> testAIConnection() {
        try {
            logger.info("Testing AI connection");
            String testPrompt = "Say 'Hello World! The DoctorSab AI system is working correctly.'";
            String result = aiService.processWithAI(testPrompt);
            
            // Ensure we never return null or empty response
            if (result == null || result.trim().isEmpty()) {
                logger.warn("Empty test AI response received");
                result = "Test completed but received empty response. API connection may have issues.";
            }
            
            return ResponseEntity.ok(new AIResponse(result));
        } catch (Exception e) {
            logger.error("Error in test endpoint", e);
            // Always return a valid response even on error
            return ResponseEntity.ok(new AIResponse("Test failed. Error: " + e.getMessage()));
        }
    }
    
    /**
     * Direct test endpoint for OpenRouter API connectivity
     * @return A direct test response from OpenRouter
     */
    @GetMapping("/test-direct")
    public ResponseEntity<AIResponse> testDirectConnection() {
        try {
            logger.info("Testing direct API connection");
            String result = aiService.testOpenRouterConnection();
            return ResponseEntity.ok(new AIResponse(result));
        } catch (Exception e) {
            logger.error("Error in direct test endpoint", e);
            return ResponseEntity.ok(new AIResponse("Direct test failed: " + e.getMessage()));
        }
    }
    
    /**
     * Simple request class for AI processing
     */
    public static class AIRequest {
        private String prompt;
        
        public AIRequest() {}
        
        public AIRequest(String prompt) {
            this.prompt = prompt;
        }
        
        public String getPrompt() {
            return prompt;
        }
        
        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }
      /**
     * Simple response class for AI processing
     */
    public static class AIResponse {
        private String response;
        
        public AIResponse() {}
        
        public AIResponse(String response) {
            this.response = response;
        }
        
        public String getResponse() {
            return response;
        }
        
        public void setResponse(String response) {
            this.response = response;
        }
        
        @Override
        public String toString() {
            return "{\"response\":\"" + response.replace("\"", "\\\"").replace("\n", "\\n") + "\"}";
        }
    }
}
