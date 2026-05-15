package com.uniride.uniridebookingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UnirideBookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnirideBookingServiceApplication.class, args);
    }

}
