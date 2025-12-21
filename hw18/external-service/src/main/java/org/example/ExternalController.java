package org.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExternalController {

    @GetMapping("/api/data")
    public ExternalResponse getData() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new ExternalResponse("OK from external service on port 9090", System.currentTimeMillis());
    }
}
