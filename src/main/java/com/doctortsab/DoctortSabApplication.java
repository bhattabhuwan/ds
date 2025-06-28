package com.doctortsab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) // Temporarily disable security for development
public class DoctortSabApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoctortSabApplication.class, args);
    }
}
