package org.example;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class ExternalClientFallbackFactory implements FallbackFactory<ExternalClient> {

    @Override
    public ExternalClient create(Throwable cause) {
        return new ExternalClient() {
            @Override
            public ExternalResponse getData() {
                String error = "FALLBACK: external service unavailable";
                if (cause != null && cause.getMessage() != null) {
                    error += " - " + cause.getMessage();
                }
                return new ExternalResponse(error, System.currentTimeMillis());
            }
        };
    }
}
