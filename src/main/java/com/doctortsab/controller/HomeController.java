package com.doctortsab.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/appointment")
    public String appointment() {
        return "appointment";
    }
    
    @GetMapping("/dashboard/user")
    public String userDashboard() {
        return "dashboard/user";
    }
    
    @GetMapping("/profile")
    public String userProfile() {
        return "profile";
    }
      @GetMapping("/logout")
    public String logout() {
        // Since we're using Firebase authentication, just redirect to home page
        return "redirect:/?logout=success";
    }
    
    @GetMapping("/symptom-check")
    public String symptomCheck() {
        return "symptom-check";
    }
    
    @GetMapping("/first-aid")
    public String firstAid() {
        return "first-aid";
    }
      @GetMapping("/health-tips")
    public String healthTips() {
        return "health-tips";
    }
    
    @PostMapping("/api/appointments/book")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> bookAppointment(@RequestBody Map<String, String> appointmentData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract data from request
            String doctorId = appointmentData.get("doctorId");
            String userId = appointmentData.get("userId");
            String date = appointmentData.get("date");
            String time = appointmentData.get("time");
            
            // Validate required fields
            if (doctorId == null || userId == null || date == null || time == null) {
                response.put("success", false);
                response.put("message", "Missing required fields");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Here you would typically save to database
            // For now, we'll just simulate success
            
            response.put("success", true);
            response.put("message", "Appointment booked successfully");
            response.put("appointmentId", "APT" + System.currentTimeMillis());
            response.put("confirmationNumber", "CF" + (int)(Math.random() * 100000));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to book appointment: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping(value = "/firebase-config.js")
    @ResponseBody
    public ResponseEntity<String> firebaseConfig() {
        try {
            ClassPathResource resource = new ClassPathResource("static/firebase-config.js");
            String content = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/javascript"));
            return ResponseEntity.ok().headers(headers).body(content);
        } catch (IOException e) {
            System.err.println("Error loading firebase-config.js: " + e.getMessage());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/javascript"))
                    .body("console.error('Error loading firebase-config.js');");
        }
    }
    
    @GetMapping(value = "/firebase-auth.js")
    @ResponseBody
    public ResponseEntity<String> firebaseAuth() {
        try {
            ClassPathResource resource = new ClassPathResource("static/firebase-auth.js");
            String content = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/javascript"))
                    .body(content);
        } catch (IOException e) {
            System.err.println("Error loading firebase-auth.js: " + e.getMessage());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/javascript"))
                    .body("console.error('Error loading firebase-auth.js');");
        }
    }
    
    @GetMapping(value = "/auth-ui.js")
    @ResponseBody
    public ResponseEntity<String> authUi() {
        try {
            ClassPathResource resource = new ClassPathResource("static/auth-ui.js");
            String content = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/javascript"))
                    .body(content);
        } catch (IOException e) {
            System.err.println("Error loading auth-ui.js: " + e.getMessage());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/javascript"))
                    .body("console.error('Error loading auth-ui.js');");
        }
    }
    
    @GetMapping(value = "/firestore-console.js")
    @ResponseBody
    public ResponseEntity<String> firestoreConsole() {
        try {
            ClassPathResource resource = new ClassPathResource("static/firestore-console.js");
            String content = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/javascript"))
                    .body(content);
        } catch (IOException e) {
            System.err.println("Error loading firestore-console.js: " + e.getMessage());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/javascript"))
                    .body("console.error('Error loading firestore-console.js');");
        }
    }
    
    @GetMapping(value = "/cursor.js")
    @ResponseBody
    public ResponseEntity<String> cursor() {
        try {
            ClassPathResource resource = new ClassPathResource("static/cursor.js");
            String content = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/javascript"))
                    .body(content);
        } catch (IOException e) {
            System.err.println("Error loading cursor.js: " + e.getMessage());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/javascript"))
                    .body("console.error('Error loading cursor.js');");
        }
    }
}
