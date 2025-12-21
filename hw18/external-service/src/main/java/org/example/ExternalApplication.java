package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExternalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalApplication.class, "--server.port=9090");
    }
}
