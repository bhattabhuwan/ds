package com.doctortsab.model;

import java.util.Date;

public class Doctor {
    private String id;
    private String name;
    private String email;
    private String specialization;
    private String qualification;
    private int experience; // years of experience
    private String hospital;
    private String licenseNumber;
    private boolean isActive;
    private double rating;
    private String profileImage;
    private Date createdAt;

    public Doctor() {}

    public Doctor(String id, String name, String email, String specialization, String qualification, int experience, String hospital, String licenseNumber, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experience = experience;
        this.hospital = hospital;
        this.licenseNumber = licenseNumber;
        this.isActive = isActive;
        this.createdAt = new Date();
        this.rating = 0.0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
