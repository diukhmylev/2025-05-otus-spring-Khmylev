package org.example;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "external-service",
        url = "http://localhost:9090",
        fallbackFactory = ExternalClientFallbackFactory.class
)
public interface ExternalClient {

    @GetMapping("/api/data")
    ExternalResponse getData();
}
