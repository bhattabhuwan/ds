package com.doctortsab.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doctortsab.model.Doctor;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    // Mock data for development - replace with actual database service later
    private final List<Doctor> doctors = createMockDoctors();

    @GetMapping("/available")
    public ResponseEntity<List<Doctor>> getAvailableDoctors(@RequestParam(required = false) String specialization) {
        List<Doctor> availableDoctors = doctors.stream()
                .filter(Doctor::isActive)
                .collect(Collectors.toList());
        
        if (specialization != null && !specialization.isEmpty()) {
            availableDoctors = availableDoctors.stream()
                    .filter(doctor -> doctor.getSpecialization().toLowerCase()
                            .contains(specialization.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(availableDoctors);
    }

    @GetMapping("/specializations")
    public ResponseEntity<List<String>> getSpecializations() {
        List<String> specializations = doctors.stream()
                .filter(Doctor::isActive)
                .map(Doctor::getSpecialization)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(specializations);
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable String doctorId) {
        return doctors.stream()
                .filter(doctor -> doctor.getId().equals(doctorId))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{doctorId}/slots")
    public ResponseEntity<List<String>> getAvailableSlots(@PathVariable String doctorId, 
                                                         @RequestParam String date) {
        // Mock available time slots - replace with actual availability logic
        List<String> slots = Arrays.asList(
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"
        );
        
        return ResponseEntity.ok(slots);
    }

    private List<Doctor> createMockDoctors() {
        List<Doctor> mockDoctors = new ArrayList<>();
        
        Doctor dr1 = new Doctor("dr1", "Dr. Sarah Johnson", "sarah.johnson@hospital.com", 
                               "Cardiology", "MD, FACC", 12, "City General Hospital", 
                               "LIC123456", true);
        dr1.setRating(4.8);
        dr1.setProfileImage("/images/doctors/dr-sarah.jpg");
        
        Doctor dr2 = new Doctor("dr2", "Dr. Michael Chen", "michael.chen@hospital.com", 
                               "Pediatrics", "MD, MPH", 8, "Children's Medical Center", 
                               "LIC789012", true);
        dr2.setRating(4.9);
        dr2.setProfileImage("/images/doctors/dr-michael.jpg");
        
        Doctor dr3 = new Doctor("dr3", "Dr. Emily Rodriguez", "emily.rodriguez@hospital.com", 
                               "Dermatology", "MD, FAAD", 15, "Skin Care Clinic", 
                               "LIC345678", true);
        dr3.setRating(4.7);
        dr3.setProfileImage("/images/doctors/dr-emily.jpg");
        
        Doctor dr4 = new Doctor("dr4", "Dr. David Kumar", "david.kumar@hospital.com", 
                               "Orthopedics", "MD, MS Ortho", 10, "Bone & Joint Hospital", 
                               "LIC901234", true);
        dr4.setRating(4.6);
        dr4.setProfileImage("/images/doctors/dr-david.jpg");
        
        Doctor dr5 = new Doctor("dr5", "Dr. Lisa Thompson", "lisa.thompson@hospital.com", 
                               "General Practice", "MD", 6, "Community Health Center", 
                               "LIC567890", true);
        dr5.setRating(4.5);
        dr5.setProfileImage("/images/doctors/dr-lisa.jpg");
        
        Doctor dr6 = new Doctor("dr6", "Dr. James Wilson", "james.wilson@hospital.com", 
                               "Neurology", "MD, PhD", 18, "Brain & Spine Institute", 
                               "LIC111213", true);
        dr6.setRating(4.9);
        dr6.setProfileImage("/images/doctors/dr-james.jpg");
        
        mockDoctors.addAll(Arrays.asList(dr1, dr2, dr3, dr4, dr5, dr6));
        return mockDoctors;
    }
}
