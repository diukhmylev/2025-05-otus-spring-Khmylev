package org.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private final ExternalClient externalClient;

    public DemoController(ExternalClient externalClient) {
        this.externalClient = externalClient;
    }

    @GetMapping("/call-external")
    public ExternalResponse callExternal() {
        return externalClient.getData();
    }
}
